package io.github.lizhifuabc.tenant.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis 多租户配置类
 * 配置 RedisTemplate，使用租户感知的 key 序列化器
 *
 * @author lizhifu
 */
@Configuration
public class RedisTenantConfiguration {

    /**
     * 配置支持多租户的 RedisTemplate
     *
     * @param connectionFactory Redis 连接工厂
     * @return 配置好的 RedisTemplate 实例
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        
        // 使用自定义的 key 序列化器
        redisTemplate.setKeySerializer(new TenantKeySerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        
        // 使用默认的 value 序列化器
        redisTemplate.setValueSerializer(RedisSerializer.json());
        redisTemplate.setHashValueSerializer(RedisSerializer.json());
        
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
}