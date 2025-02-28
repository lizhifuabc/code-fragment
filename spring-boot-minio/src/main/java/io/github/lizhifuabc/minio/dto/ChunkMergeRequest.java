package io.github.lizhifuabc.minio.dto;

import lombok.Data;

/**
 * 分片合并请求参数
 *
 * @author lizhifu
 * @since 2024/2/24
 */
@Data
public class ChunkMergeRequest {
    /**
     * 文件MD5 ID
     */
    private Long fileMd5Id;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 总分片数
     */
    private int totalChunks;

    /**
     * 存储桶名称（可选）
     */
    private String bucketName;
}