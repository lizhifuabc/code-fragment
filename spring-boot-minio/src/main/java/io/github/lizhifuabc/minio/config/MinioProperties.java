package io.github.lizhifuabc.minio.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * MinIO 配置属性
 *
 * @author lizhifu
 * @since 2024/2/24
 */
@Data
@ConfigurationProperties(prefix = "minio")
public class MinioProperties {
    /**
     * 端点
     */
    private String endpoint;

    /**
     * 用户名
     */
    private String accessKey;

    /**
     * 密码
     */
    private String secretKey;

    /**
     * 默认的桶名称
     */
    private String defaultBucketName;
}