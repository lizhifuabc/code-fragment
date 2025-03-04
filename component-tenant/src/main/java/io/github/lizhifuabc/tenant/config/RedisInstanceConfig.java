package io.github.lizhifuabc.tenant.config;

import io.github.lizhifuabc.tenant.context.TenantContextHolder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class RedisInstanceConfig {
    
    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        return new RedisTemplate<String, Object>() {
            @Override
            public RedisConnectionFactory getConnectionFactory() {
                String tenantId = TenantContextHolder.getTenantId();
                // 根据租户ID获取对应的Redis配置
                RedisStandaloneConfiguration config = getTenantRedisConfig(tenantId);
                return new LettuceConnectionFactory(config);
            }
        };
    }
    
    private RedisStandaloneConfiguration getTenantRedisConfig(String tenantId) {
        // 从配置中心或数据库获取租户对应的Redis配置
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(getTenantHost(tenantId));
        config.setPort(getTenantPort(tenantId));
        config.setPassword(getTenantPassword(tenantId));
        return config;
    }
}