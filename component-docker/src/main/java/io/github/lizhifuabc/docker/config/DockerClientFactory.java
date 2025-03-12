package io.github.lizhifuabc.docker.config;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Docker客户端工厂类
 * 负责创建和管理多个Docker服务器的客户端实例
 *
 * @author lizhifu
 * @since 2024/3/13
 */
@Slf4j
@Component
public class DockerClientFactory {

    /**
     * Docker客户端缓存
     * key: 服务器名称
     * value: Docker客户端实例
     */
    private final Map<String, DockerClient> clientCache = new ConcurrentHashMap<>();

    /**
     * Docker配置属性
     */
    private final DockerProperties dockerProperties;

    public DockerClientFactory(DockerProperties dockerProperties) {
        this.dockerProperties = dockerProperties;
        // 初始化所有配置的Docker客户端
        initializeClients();
    }

    /**
     * 获取指定服务器的Docker客户端
     *
     * @param serverName 服务器名称
     * @return Docker客户端实例
     */
    public DockerClient getClient(String serverName) {
        return clientCache.get(serverName);
    }

    /**
     * 获取所有已配置的Docker服务器名称
     *
     * @return Docker服务器名称列表
     */
    public List<String> getServers() {
        return new ArrayList<>(clientCache.keySet());
    }

    /**
     * 初始化所有配置的Docker客户端
     */
    private void initializeClients() {
        for (DockerProperties.DockerServerConfig serverConfig : dockerProperties.getServers()) {
            try {
                DockerClient client = createDockerClient(serverConfig);
                clientCache.put(serverConfig.getName(), client);
                log.info("Docker客户端初始化成功: {}", serverConfig.getName());
            } catch (Exception e) {
                log.error("Docker客户端初始化失败: {}", serverConfig.getName(), e);
            }
        }
    }

    /**
     * 创建Docker客户端实例
     *
     * @param serverConfig 服务器配置
     * @return Docker客户端实例
     */
    private DockerClient createDockerClient(DockerProperties.DockerServerConfig serverConfig) {
        // 创建Docker客户端配置构建器
        DefaultDockerClientConfig.Builder configBuilder = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost(serverConfig.getHost())
                .withApiVersion(serverConfig.getApiVersion());

        // 配置TLS相关参数
        if (serverConfig.isTlsEnabled()) {
            configBuilder
                    .withDockerTlsVerify(serverConfig.isTlsVerify())
                    .withDockerCertPath(serverConfig.getTlsCertPath());
        }

        // 构建Docker客户端配置
        DefaultDockerClientConfig config = configBuilder.build();

        // 创建Docker HTTP客户端
        ApacheDockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost())
                .sslConfig(config.getSSLConfig())
                .maxConnections(dockerProperties.getMaxConnections())
                .connectionTimeout(Duration.ofMillis(dockerProperties.getConnectTimeout()))
                .responseTimeout(Duration.ofMillis(dockerProperties.getReadTimeout()))
                .build();

        // 创建并返回Docker客户端实例
        return DockerClientImpl.getInstance(config, httpClient);
    }
}