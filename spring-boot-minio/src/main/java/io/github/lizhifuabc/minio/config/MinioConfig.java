package io.github.lizhifuabc.minio.config;

import io.minio.MinioClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MinIO 配置类
 *
 * @author lizhifu
 * @since 2024/2/24
 */
@Configuration
@EnableConfigurationProperties(MinioProperties.class)
public class MinioConfig {
    /**
     * 注册MinioClient
     */
    @Bean
    public MinioClient minioClient(MinioProperties minioProperties) {
        return MinioClient.builder()
                .endpoint(minioProperties.getEndpoint())
                .credentials(minioProperties.getAccessKey(), minioProperties.getSecretKey())
                .build();
    }
}