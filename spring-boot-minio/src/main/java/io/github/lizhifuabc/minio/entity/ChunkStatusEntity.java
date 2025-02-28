package io.github.lizhifuabc.minio.entity;

import jakarta.persistence.*;
import lombok.Data;

/**
 * 分片上传状态实体
 * 用于存储文件分片的上传状态
 *
 * @author lizhifu
 * @since 2024/2/24
 */
@Data
@Entity
@Table(name = "chunk_status")
public class ChunkStatusEntity {
    /**
     * 自增主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 文件MD5实体，关联FileMd5Entity
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_md5_id", referencedColumnName = "id")
    private FileMd5Entity fileMd5Entity;

    /**
     * 分片编号
     */
    private Integer chunkNumber;

    /**
     * 总分片数
     */
    private Integer totalChunks;

    /**
     * 上传状态
     */
    private Boolean uploaded;
}