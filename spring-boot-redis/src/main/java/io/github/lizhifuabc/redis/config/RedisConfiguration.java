package io.github.lizhifuabc.redis.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Redis 配置类
 * 提供 Redis 序列化、反序列化及连接工厂等相关配置
 *
 * @author lizhifu
 * @since 2025/2/24
 */
@Configuration
public class RedisConfiguration {
    /** 默认日期时间格式 */
    private static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    /** 默认日期格式 */
    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    /** 默认时间格式 */
    private static final String DEFAULT_TIME_FORMAT = "HH:mm:ss";

    /**
     * 创建 RedisTemplate Bean，用于 Redis 数据操作
     * 配置内容包括：
     * 1. 使用 Lettuce 作为 Redis 客户端
     * 2. 设置 key 使用字符串序列化
     * 3. 设置 value 使用 JSON 序列化，并支持 Java8 时间类型
     * 4. 配置类型转换，确保反序列化时能够正确恢复对象类型
     *
     * @param lettuceConnectionFactory Lettuce 连接工厂
     * @return 配置完成的 RedisTemplate 实例
     */
    @Bean(name = "redisTemplate")
    @ConditionalOnMissingBean(name = "redisTemplate")
    public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory lettuceConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        // 设置 Redis 连接工厂，使用 Lettuce 客户端，它是线程安全的，支持异步操作
        redisTemplate.setConnectionFactory(lettuceConnectionFactory);

        // 创建 ObjectMapper 并配置序列化规则
        ObjectMapper objectMapper = new ObjectMapper();
        // 设置所有访问权限，防止出现私有属性无法序列化的问题
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // 启用默认类型，将类信息作为属性写入，确保反序列化时能够正确恢复对象类型
        objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY);

        // 配置 Java8 日期时间类型的序列化和反序列化
        SimpleModule simpleModule = new SimpleModule();
        // LocalDateTime 序列化配置，确保日期时间格式统一
        simpleModule.addSerializer(LocalDateTime.class,
                new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT)));
        simpleModule.addSerializer(LocalDate.class, 
                new LocalDateSerializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT)));
        simpleModule.addSerializer(LocalTime.class, 
                new LocalTimeSerializer(DateTimeFormatter.ofPattern(DEFAULT_TIME_FORMAT)));
        simpleModule.addDeserializer(LocalDateTime.class, 
                new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT)));
        simpleModule.addDeserializer(LocalDate.class, 
                new LocalDateDeserializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT)));
        simpleModule.addDeserializer(LocalTime.class, 
                new LocalTimeDeserializer(DateTimeFormatter.ofPattern(DEFAULT_TIME_FORMAT)));
        objectMapper.registerModule(simpleModule);

        // 创建 JSON 序列化器，使用 GenericJackson2JsonRedisSerializer 支持复杂对象的序列化
        // 它会在序列化时保存类型信息，确保反序列化时能够正确还原对象
        RedisSerializer<Object> jsonRedisSerializer = new GenericJackson2JsonRedisSerializer(objectMapper);
        
        // 设置 key 的序列化方式为 String，因为 Redis 的 key 一般使用字符串，这样方便查看和管理
        redisTemplate.setKeySerializer(RedisSerializer.string());
        // Hash 结构的 field 也使用 String 序列化器，原因同上
        redisTemplate.setHashKeySerializer(RedisSerializer.string());
        // value 使用 JSON 序列化器，支持复杂对象的存储，并保留类型信息
        redisTemplate.setValueSerializer(jsonRedisSerializer);
        // Hash 结构的 value 也使用 JSON 序列化器，确保复杂对象的正确存储和还原
        redisTemplate.setHashValueSerializer(jsonRedisSerializer);
        // 初始化 RedisTemplate，检查配置是否正确
        redisTemplate.afterPropertiesSet();
        
        return redisTemplate;
    }
}
