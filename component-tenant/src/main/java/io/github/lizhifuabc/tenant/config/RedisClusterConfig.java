package io.github.lizhifuabc.tenant.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

@Configuration
public class RedisClusterConfig {

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        RedisClusterConfiguration clusterConfig = new RedisClusterConfiguration();
        // 配置集群节点
        clusterConfig.clusterNode("host1", 6379);
        clusterConfig.clusterNode("host2", 6379);
        clusterConfig.clusterNode("host3", 6379);
        
        return new LettuceConnectionFactory(clusterConfig);
    }
}