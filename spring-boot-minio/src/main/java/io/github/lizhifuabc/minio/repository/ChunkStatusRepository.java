package io.github.lizhifuabc.minio.repository;

import io.github.lizhifuabc.minio.entity.ChunkStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 分片上传状态Repository
 * 处理分片上传状态的数据库操作
 *
 * @author lizhifu
 * @since 2024/2/24
 */
@Repository
public interface ChunkStatusRepository extends JpaRepository<ChunkStatusEntity, Long> {
    /**
     * 根据FileMd5Entity的ID查找所有分片状态
     * @param fileMd5Id FileMd5Entity的ID
     * @return 分片状态列表
     */
    List<ChunkStatusEntity> findByFileMd5EntityId(Long fileMd5Id);

    /**
     * 根据FileMd5Entity的ID和分片编号查找分片状态
     * @param fileMd5Id FileMd5Entity的ID
     * @param chunkNumber 分片编号
     * @return 分片状态
     */
    ChunkStatusEntity findByFileMd5EntityIdAndChunkNumber(Long fileMd5Id, Integer chunkNumber);

    /**
     * 根据FileMd5Entity的ID删除所有分片状态
     * @param fileMd5Id FileMd5Entity的ID
     */
    @Transactional
    void deleteByFileMd5EntityId(Long fileMd5Id);
}