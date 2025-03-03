package io.github.lizhifuabc.redis.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * JSON工具类
 *
 * @author lizhifu
 */
@Slf4j
@Component
public class JsonUtils {
    private final ObjectMapper objectMapper;

    public JsonUtils(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * 序列化对象为JSON字符串
     */
    public String toJsonStr(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            log.error("JSON序列化失败", e);
            throw new RuntimeException("JSON序列化失败", e);
        }
    }

    /**
     * 反序列化JSON为对象
     */
    public <T> T toBean(String json, Class<T> type) {
        try {
            return objectMapper.readValue(json, type);
        } catch (JsonProcessingException e) {
            log.error("JSON反序列化失败", e);
            throw new RuntimeException("JSON反序列化失败", e);
        }
    }

    /**
     * 反序列化JSON为List
     */
    public <T> List<T> toBeanList(String json, Class<T> type) {
        try {
            JavaType javaType = objectMapper.getTypeFactory().constructCollectionType(List.class, type);
            return objectMapper.readValue(json, javaType);
        } catch (JsonProcessingException e) {
            log.error("JSON反序列化失败", e);
            throw new RuntimeException("JSON反序列化失败", e);
        }
    }
}