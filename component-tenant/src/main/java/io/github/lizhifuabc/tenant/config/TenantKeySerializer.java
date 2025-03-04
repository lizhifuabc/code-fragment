package io.github.lizhifuabc.tenant.config;

import io.github.lizhifuabc.tenant.context.TenantContextHolder;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.StringUtils;

/**
 * 租户感知的 Redis Key 序列化器
 * 主要功能：
 * 1. 自动为 key 添加租户前缀
 * 2. 确保多租户数据隔离
 * 3. 支持自定义序列化策略
 *
 * 使用示例：
 * redisTemplate.opsForValue().set("user:1", value);
 * 实际存储的 key 为：${tenantId}:user:1
 *
 * @author lizhifu
 */
public class TenantKeySerializer extends StringRedisSerializer {
    
    private static final String TENANT_SEPARATOR = ":";
    
    /**
     * 序列化 key，自动添加租户前缀
     *
     * @param key 原始 key
     * @return 添加租户前缀后的 key 的字节数组
     */
    @Override
    public byte[] serialize(String key) {
        if (key == null) {
            return null;
        }
        String tenantId = TenantContextHolder.getTenantId();
        if (StringUtils.hasText(tenantId)) {
            key = tenantId + TENANT_SEPARATOR + key;
        }
        return super.serialize(key);
    }

    /**
     * 反序列化 key，自动移除租户前缀
     *
     * @param bytes key的字节数组
     * @return 移除租户前缀后的原始 key
     */
    @Override
    public String deserialize(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        String key = super.deserialize(bytes);
        if (key == null) {
            return null;
        }
        
        String tenantId = TenantContextHolder.getTenantId();
        if (StringUtils.hasText(tenantId) && key.startsWith(tenantId + TENANT_SEPARATOR)) {
            return key.substring(tenantId.length() + TENANT_SEPARATOR.length());
        }
        return key;
    }

    /**
     * 从完整的key中提取租户ID
     *
     * @param fullKey 完整的key
     * @return 租户ID，如果没有租户前缀则返回null
     */
    public static String extractTenantId(String fullKey) {
        if (!StringUtils.hasText(fullKey)) {
            return null;
        }
        int separatorIndex = fullKey.indexOf(TENANT_SEPARATOR);
        return separatorIndex > 0 ? fullKey.substring(0, separatorIndex) : null;
    }
}