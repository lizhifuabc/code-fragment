package io.github.lizhifuabc.minio.service;

import io.github.lizhifuabc.minio.config.MinioProperties;
import io.github.lizhifuabc.minio.entity.ChunkStatusEntity;
import io.github.lizhifuabc.minio.entity.FileMd5Entity;
import io.github.lizhifuabc.minio.entity.FileMd5Entity.UploadStatus;
import io.github.lizhifuabc.minio.repository.ChunkStatusRepository;
import io.github.lizhifuabc.minio.repository.FileMd5Repository;
import io.minio.ComposeObjectArgs;
import io.minio.ComposeSource;
import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 高级文件上传服务
 * 支持秒传、断点续传、分片上传
 *
 * @author lizhifu
 * @since 2024/2/24
 */
@Service
@RequiredArgsConstructor
public class AdvancedUploadService {
    private final MinioClient minioClient;
    private final MinioProperties minioProperties;
    private final MinioService minioService;
    private final FileMd5Repository fileMd5Repository;
    private final ChunkStatusRepository chunkStatusRepository;

    /**
     * 尝试秒传文件
     * @param file 文件
     * @param bucketName 存储桶名称
     * @return 如果秒传成功返回true，否则返回false
     */
    @SneakyThrows
    public Long tryFastUpload(String md5, String fileName) {
        FileMd5Entity entity = fileMd5Repository.findByMd5(md5);
        
        if (entity != null && entity.getUploadStatus() == UploadStatus.COMPLETED) {
            // 文件已存在且完整上传，直接返回ID
            return entity.getId();
        }

        // 文件不存在或未完成上传，创建新记录
        if (entity == null) {
            entity = new FileMd5Entity();
            entity.setMd5(md5);
            entity.setFilePath(fileName);
            entity.setUploadStatus(UploadStatus.INITIALIZED);
            entity = fileMd5Repository.save(entity);
        }
        return entity.getId();
    }

    /**
     * 上传分片
     * @param chunk 分片数据
     * @param chunkNumber 分片编号
     * @param totalChunks 总分片数
     * @param fileMd5Id 文件唯一标识
     * @param bucketName 存储桶名称
     */
    @SneakyThrows
    public void uploadChunk(MultipartFile chunk, int chunkNumber, int totalChunks, 
                          Long fileMd5Id, String bucketName) {
        if (bucketName == null) {
            bucketName = minioProperties.getDefaultBucketName();
        }

        // 确保存储桶存在
        minioService.createBucket(bucketName);

        // 获取FileMd5Entity
        FileMd5Entity fileMd5Entity = fileMd5Repository.findById(fileMd5Id)
            .orElseThrow(() -> new IllegalArgumentException("File MD5 record not found"));

        // 设置状态为上传中
        if (fileMd5Entity.getUploadStatus() == UploadStatus.INITIALIZED) {
            fileMd5Entity.setUploadStatus(UploadStatus.UPLOADING);
            fileMd5Repository.save(fileMd5Entity);
        }

        // 上传分片
        String chunkObjectName = String.format("%d/chunk_%d", fileMd5Id, chunkNumber);
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(chunkObjectName)
                        .stream(chunk.getInputStream(), chunk.getSize(), -1)
                        .build()
        );

        // 更新分片状态
        ChunkStatusEntity entity = new ChunkStatusEntity();
        entity.setFileMd5Entity(fileMd5Entity);
        entity.setChunkNumber(chunkNumber);
        entity.setTotalChunks(totalChunks);
        entity.setUploaded(true);
        chunkStatusRepository.save(entity);
    }

    /**
     * 检查分片上传状态
     * @param fileId 文件唯一标识
     * @param totalChunks 总分片数
     * @return 已上传的分片列表
     */
    public List<Integer> getUploadedChunks(Long fileMd5Id, int totalChunks) {
        List<ChunkStatusEntity> chunks = chunkStatusRepository.findByFileMd5EntityId(fileMd5Id);
        return chunks.stream()
                .filter(chunk -> chunk.getUploaded())
                .map(ChunkStatusEntity::getChunkNumber)
                .toList();
    }

    /**
     * 合并分片
     * @param fileMd5Id FileMd5Entity的ID
     * @param fileName 最终文件名
     * @param totalChunks 总分片数
     * @param bucketName 存储桶名称
     * @return 合并后的文件信息
     */
    @SneakyThrows
    public ObjectWriteResponse mergeChunks(Long fileMd5Id, String fileName, 
                                         int totalChunks, String bucketName) {
        if (bucketName == null) {
            bucketName = minioProperties.getDefaultBucketName();
        }

        // 创建合并源
        List<ComposeSource> sources = new ArrayList<>();
        for (int i = 0; i < totalChunks; i++) {
            String chunkObjectName = String.format("%d/chunk_%d", fileMd5Id, i);
            sources.add(
                ComposeSource.builder()
                    .bucket(bucketName)
                    .object(chunkObjectName)
                    .build()
            );
        }

        // 合并文件
        ObjectWriteResponse response = minioClient.composeObject(
                ComposeObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fileName)
                        .sources(sources)
                        .build()
        );

        // 清理分片
        for (int i = 0; i < totalChunks; i++) {
            String chunkObjectName = String.format("%d/chunk_%d", fileMd5Id, i);
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(chunkObjectName)
                            .build()
            );
        }

        // 清理状态记录
        chunkStatusRepository.deleteByFileMd5EntityId(fileMd5Id);

        // 记录文件MD5，用于秒传
        String md5 = calculateMD5(minioService.downloadFile(bucketName, fileName));
        FileMd5Entity entity = fileMd5Repository.findById(fileMd5Id)
            .orElseGet(FileMd5Entity::new);
        entity.setMd5(md5);
        entity.setFilePath(fileName);
        entity.setUploadStatus(UploadStatus.MERGED);
        fileMd5Repository.save(entity);

        return response;
    }

    /**
     * 计算文件MD5
     * @param inputStream 文件输入流
     * @return MD5值
     */
    private String calculateMD5(InputStream inputStream) throws IOException {
        return DigestUtils.md5DigestAsHex(inputStream);
    }
}