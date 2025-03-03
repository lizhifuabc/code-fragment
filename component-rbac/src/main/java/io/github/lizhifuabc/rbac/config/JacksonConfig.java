package io.github.lizhifuabc.rbac.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.time.ZoneId;
import java.util.TimeZone;

/**
 * Jackson 配置
 * 1. Long 类型转 String（避免前端精度丢失）
 * 2. 时间类型格式化
 * 3. BigInteger 转 String
 *
 * @author lizhifu
 * @since 2024/2/24
 */
@Configuration
public class JacksonConfig {
    /**
     * 默认日期时间格式
     */
    public static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    /**
     * 默认日期格式
     */
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    /**
     * 默认时间格式
     */
    public static final String DEFAULT_TIME_FORMAT = "HH:mm:ss";

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        
        // 注册 JavaTimeModule
        JavaTimeModule javaTimeModule = new JavaTimeModule();

        // LocalDateTime 序列化和反序列化配置
        // 序列化：将 LocalDateTime 转换为 "yyyy-MM-dd HH:mm:ss" 格式的字符串
        // 反序列化：将 "yyyy-MM-dd HH:mm:ss" 格式的字符串转换为 LocalDateTime 对象
        javaTimeModule.addSerializer(LocalDateTime.class, 
            new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT)));
        javaTimeModule.addDeserializer(LocalDateTime.class,
            new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT)));

        // LocalDate 序列化和反序列化配置
        // 序列化：将 LocalDate 转换为 "yyyy-MM-dd" 格式的字符串
        // 反序列化：将 "yyyy-MM-dd" 格式的字符串转换为 LocalDate 对象
        javaTimeModule.addSerializer(LocalDate.class, 
            new LocalDateSerializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT)));
        javaTimeModule.addDeserializer(LocalDate.class,
            new LocalDateDeserializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT)));

        // LocalTime 序列化和反序列化配置
        // 序列化：将 LocalTime 转换为 "HH:mm:ss" 格式的字符串
        // 反序列化：将 "HH:mm:ss" 格式的字符串转换为 LocalTime 对象
        javaTimeModule.addSerializer(LocalTime.class, 
            new LocalTimeSerializer(DateTimeFormatter.ofPattern(DEFAULT_TIME_FORMAT)));
        javaTimeModule.addDeserializer(LocalTime.class,
            new LocalTimeDeserializer(DateTimeFormatter.ofPattern(DEFAULT_TIME_FORMAT)));

        // Long 类型序列化配置
        // 将 Long 类型转换为字符串，避免前端 JavaScript 处理大数时精度丢失
        // Long.class 处理包装类型
        javaTimeModule.addSerializer(Long.class, ToStringSerializer.instance);
        // Long.TYPE 处理基本类型
        javaTimeModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
        
        // BigInteger 序列化配置
        // 将 BigInteger 转换为字符串，避免前端 JavaScript 处理大数时精度丢失
        javaTimeModule.addSerializer(BigInteger.class, ToStringSerializer.instance);

        // 注册自定义的序列化和反序列化配置
        objectMapper.registerModule(javaTimeModule);
        
        // 增加通用序列化配置
        // 反序列化时忽略未知属性，避免因为 JSON 中存在 Java 对象中不存在的字段而导致反序列化失败
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 允许序列化空对象，避免因为对象中没有任何可序列化字段而导致序列化失败
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        // 日期类型不转为时间戳，而是使用 ISO-8601 格式字符串
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        // 序列化时忽略 null 值字段，减少数据传输量
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        
        // 设置时区为系统默认
        objectMapper.setTimeZone(TimeZone.getTimeZone(ZoneId.systemDefault()));
        
        return objectMapper;
    }
}