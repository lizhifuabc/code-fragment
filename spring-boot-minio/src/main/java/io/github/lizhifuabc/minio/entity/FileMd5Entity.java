package io.github.lizhifuabc.minio.entity;

import jakarta.persistence.*;
import lombok.Data;

/**
 * 文件MD5映射实体
 * 用于存储文件的MD5值和对应的文件路径
 *
 * @author lizhifu
 * @since 2024/2/24
 */
@Data
@Entity
@Table(name = "file_md5_mapping")
public class FileMd5Entity {
    /**
     * 自增主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 文件的MD5值，设置为唯一索引
     */
    @Column(unique = true, nullable = false)
    private String md5;

    /**
     * 文件在MinIO中的路径
     */
    private String filePath;

    /**
     * 文件上传状态
     * INCOMPLETE: 未完成
     * UPLOADING: 上传中
     * COMPLETED: 已完成
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UploadStatus uploadStatus = UploadStatus.INITIALIZED;

    public enum UploadStatus {
        /**
         * 初始化状态
         */
        INITIALIZED,
        /**
         * 上传中
         */
        UPLOADING,
        /**
         * 上传失败
         */
        FAILED,
        /**
         * 上传完成
         */
        COMPLETED,
        /**
         * 合并完成
         */
        MERGED
    }
}