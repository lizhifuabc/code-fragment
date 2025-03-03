package io.github.lizhifuabc.rbac.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.Resource;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis 配置
 * 1. 配置 RedisTemplate，使用 Jackson2JsonRedisSerializer 序列化
 * 2. 开启缓存注解功能
 *
 * @author lizhifu
 * @since 2024/2/24
 */
@EnableCaching
@Configuration
public class RedisConfig {

    @Resource
    private RedisConnectionFactory factory;

    /**
     * 配置自定义redisTemplate
     * 使用方式：
     * - 注入 RedisTemplate 即可使用
     * - 支持 Redis 各种数据结构的操作
     * - 使用 Jackson2JsonRedisSerializer 序列化，支持复杂对象的存储
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        // 创建 ObjectMapper 实例，用于 JSON 序列化配置
        ObjectMapper om = new ObjectMapper();
        om.registerModule(new JavaTimeModule())  // 注册 Java 8 时间模块
                .configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false)  // 时间戳不使用纳秒
                .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)  // 允许序列化空对象
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)  // 忽略 JSON 中存在但 Java 对象不存在的属性
                .configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false)  // 解析时间戳不使用纳秒
                .setSerializationInclusion(JsonInclude.Include.NON_NULL);  // 序列化时忽略 null 值

        // 设置所有访问器的可见性为 ANY
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // 启用默认类型，保存序列化后的类型信息
        om.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);

        // 创建支持泛型的 JSON 序列化器
        return getRedisTemplate(om);
    }

    private RedisTemplate<String, Object> getRedisTemplate(ObjectMapper om) {
        GenericJackson2JsonRedisSerializer jsonRedisSerializer = new GenericJackson2JsonRedisSerializer(om);

        // 创建 Redis 模板
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        // 设置 Redis 连接工厂
        template.setConnectionFactory(factory);

        // 创建字符串序列化器
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        // 设置 key 使用字符串序列化器
        template.setKeySerializer(stringRedisSerializer);
        // 设置 value 使用 JSON 序列化器
        template.setValueSerializer(jsonRedisSerializer);
        // 设置 hash key 使用字符串序列化器
        template.setHashKeySerializer(stringRedisSerializer);
        // 设置 hash value 使用 JSON 序列化器
        template.setHashValueSerializer(jsonRedisSerializer);
        // 设置默认序列化器为字符串序列化器
        template.setDefaultSerializer(stringRedisSerializer);

        // 初始化 RedisTemplate
        template.afterPropertiesSet();
        return template;
    }

    /**
     * Hash 操作
     */
    @Bean
    public HashOperations<String, String, Object> hashOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForHash();
    }

    /**
     * String 操作
     */
    @Bean
    public ValueOperations<String, String> valueOperations(RedisTemplate<String, String> redisTemplate) {
        return redisTemplate.opsForValue();
    }

    /**
     * List 操作
     */
    @Bean
    public ListOperations<String, Object> listOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForList();
    }

    /**
     * Set 操作
     */
    @Bean
    public SetOperations<String, Object> setOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForSet();
    }

    /**
     * ZSet 操作
     */
    @Bean
    public ZSetOperations<String, Object> zSetOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForZSet();
    }
}