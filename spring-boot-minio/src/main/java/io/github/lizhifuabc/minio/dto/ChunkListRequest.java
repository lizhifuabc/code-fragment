package io.github.lizhifuabc.minio.dto;

import lombok.Data;

/**
 * 获取分片列表请求参数
 *
 * @author lizhifu
 * @since 2024/2/24
 */
@Data
public class ChunkListRequest {
    /**
     * 文件MD5 ID
     */
    private Long fileMd5Id;

    /**
     * 总分片数
     */
    private int totalChunks;
}