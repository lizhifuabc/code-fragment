package io.github.lizhifuabc.tenant.config;

import io.github.lizhifuabc.tenant.context.TenantContextHolder;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.StringUtils;

/**
 * 租户感知的 Redis Key 序列化器
 * 负责在 Redis key 前自动添加租户标识
 *
 * @author lizhifu
 */
public class TenantKeySerializer extends StringRedisSerializer {

    /**
     * 序列化 key，自动添加租户前缀
     *
     * @param key 原始 key
     * @return 添加租户前缀后的 key 的字节数组
     */
    @Override
    public byte[] serialize(String key) {
        String tenantId = TenantContextHolder.getTenantId();
        if (StringUtils.hasText(tenantId)) {
            key = tenantId + ":" + key;
        }
        return super.serialize(key);
    }
}