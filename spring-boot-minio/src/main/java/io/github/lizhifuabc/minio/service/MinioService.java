package io.github.lizhifuabc.minio.service;

import io.github.lizhifuabc.minio.config.MinioProperties;
import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.Bucket;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * MinIO 操作服务类
 *
 * @author lizhifu
 * @since 2024/2/24
 */
@Service
@RequiredArgsConstructor
public class MinioService {
    private final MinioClient minioClient;
    private final MinioProperties minioProperties;

    /**
     * 创建存储桶
     */
    @SneakyThrows
    public void createBucket(String bucketName) {
        boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        if (!exists) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        }
    }

    /**
     * 上传文件
     */
    @SneakyThrows
    public ObjectWriteResponse uploadFile(MultipartFile file, String bucketName, String objectName) {
        // 使用默认存储桶
        if (bucketName == null) {
            bucketName = minioProperties.getDefaultBucketName();
        }
        createBucket(bucketName);
        InputStream inputStream = file.getInputStream();
        return minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .stream(inputStream, file.getSize(), -1)
                        .contentType(file.getContentType())
                        .build());
    }

    /**
     * 下载文件
     */
    @SneakyThrows
    public InputStream downloadFile(String bucketName, String objectName) {
        return minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .build());
    }

    /**
     * 删除文件
     */
    @SneakyThrows
    public void deleteFile(String bucketName, String objectName) {
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .build());
    }

    /**
     * 获取文件访问URL
     */
    @SneakyThrows
    public String getFileUrl(String bucketName, String objectName, Integer expires) {
        return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Method.GET)
                        .bucket(bucketName)
                        .object(objectName)
                        .expiry(expires, TimeUnit.SECONDS)
                        .build());
    }

    /**
     * 列出所有存储桶
     */
    @SneakyThrows
    public List<Bucket> listBuckets() {
        return minioClient.listBuckets();
    }

    /**
     * 列出存储桶中的所有对象
     */
    @SneakyThrows
    public Iterable<Result<Item>> listObjects(String bucketName) {
        return minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(bucketName)
                        .build());
    }

    /**
     * 批量删除文件
     */
    @SneakyThrows
    public void deleteFiles(String bucketName, List<String> objectNames) {
        List<DeleteObject> objects = new ArrayList<>();
        objectNames.forEach(name -> objects.add(new DeleteObject(name)));
        minioClient.removeObjects(
                RemoveObjectsArgs.builder()
                        .bucket(bucketName)
                        .objects(objects)
                        .build());
    }
}