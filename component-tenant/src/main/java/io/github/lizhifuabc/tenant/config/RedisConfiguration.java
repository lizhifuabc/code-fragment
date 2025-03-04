package io.github.lizhifuabc.tenant.config;

import io.github.lizhifuabc.tenant.service.TenantConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis 统一配置类
 * 支持多种租户隔离策略：PREFIX、DATABASE、INSTANCE、CLUSTER
 *
 * @author lizhifu
 */
@Configuration
public class RedisConfiguration {

    @Autowired
    private TenantConfigService tenantConfigService;

    @Bean
    @Primary
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory());

        // 设置序列化器
        template.setKeySerializer(new TenantKeySerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(RedisSerializer.json());
        template.setHashValueSerializer(RedisSerializer.json());

        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new DynamicLettuceConnectionFactory(tenantConfigService);
    }
}