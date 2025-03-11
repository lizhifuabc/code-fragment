package io.github.lizhifuabc.docker.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * Docker配置属性
 *
 * @author lizhifu
 * @since 2024/3/13
 */
@Data
@ConfigurationProperties(prefix = "docker")
public class DockerProperties {
    /**
     * Docker服务器配置列表
     */
    private List<DockerServerConfig> servers = new ArrayList<>();

    /**
     * 连接超时时间（毫秒）
     */
    private Integer connectTimeout = 5000;

    /**
     * 读取超时时间（毫秒）
     */
    private Integer readTimeout = 30000;

    /**
     * 最大连接数
     */
    private Integer maxConnections = 100;

    /**
     * Docker服务器配置
     */
    @Data
    public static class DockerServerConfig {
        /**
         * 服务器名称（唯一标识）
         */
        private String name;

        /**
         * Docker主机地址
         */
        private String host = "unix:///var/run/docker.sock";

        /**
         * Docker API版本
         */
        private String apiVersion = "1.41";

        /**
         * 是否启用TLS
         */
        private boolean tlsEnabled = false;

        /**
         * CA证书路径
         */
        private String tlsCaCertPath;

        /**
         * 客户端证书路径
         */
        private String tlsCertPath;

        /**
         * 客户端私钥路径
         */
        private String tlsKeyPath;

        /**
         * 是否验证服务器证书
         */
        private boolean tlsVerify = true;
    }
}