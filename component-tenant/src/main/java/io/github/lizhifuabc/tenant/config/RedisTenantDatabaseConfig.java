package io.github.lizhifuabc.tenant.config;

import io.github.lizhifuabc.tenant.context.TenantContextHolder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class RedisTenantDatabaseConfig {

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(new RedisStandaloneConfiguration()) {
            @Override
            public RedisConnection getConnection() {
                RedisConnection connection = super.getConnection();
                // 根据租户ID设置不同的database
                connection.select(getTenantDatabase());
                return connection;
            }
        };
    }
    
    private int getTenantDatabase() {
        String tenantId = TenantContextHolder.getTenantId();
        // 可以通过配置或其他方式将租户ID映射到具体的database
        return Math.abs(tenantId.hashCode() % 16); // Redis默认支持16个数据库
    }
}