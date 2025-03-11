package io.github.lizhifuabc.docker.config;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Docker客户端配置
 *
 * @author lizhifu
 * @since 2024/3/13
 */
@Configuration
@EnableConfigurationProperties(DockerProperties.class)
public class DockerClientConfig {

    @Bean
    public DockerClientFactory dockerClientFactory(DockerProperties properties) {
        return new DockerClientFactory(properties);
    }
}