package io.github.lizhifuabc.web.idempotent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 幂等性服务
 * 用于确保API接口的幂等性，防止重复提交
 *
 * @author lizhifu
 * @since 2025/3/19
 */
@Service
public class IdempotentService {
    
    /**
     * Redis模板
     */
    private final StringRedisTemplate redisTemplate;
    
    /**
     * 幂等性键前缀
     */
    private static final String IDEMPOTENT_KEY_PREFIX = "idempotent:";
    
    /**
     * 默认过期时间（秒）
     */
    private static final long DEFAULT_EXPIRE_SECONDS = 60;

    @Autowired
    public IdempotentService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 检查请求是否重复
     *
     * @param requestId 请求ID
     * @return 是否为重复请求
     */
    public boolean isRepeatedRequest(String requestId) {
        String key = IDEMPOTENT_KEY_PREFIX + requestId;
        Boolean isAbsent = redisTemplate.opsForValue().setIfAbsent(key, "1", DEFAULT_EXPIRE_SECONDS, TimeUnit.SECONDS);
        return isAbsent == null || !isAbsent;
    }

    /**
     * 检查请求是否重复
     *
     * @param requestId 请求ID
     * @param expireSeconds 过期时间（秒）
     * @return 是否为重复请求
     */
    public boolean isRepeatedRequest(String requestId, long expireSeconds) {
        String key = IDEMPOTENT_KEY_PREFIX + requestId;
        Boolean isAbsent = redisTemplate.opsForValue().setIfAbsent(key, "1", expireSeconds, TimeUnit.SECONDS);
        return isAbsent == null || !isAbsent;
    }

    /**
     * 删除幂等性标记
     *
     * @param requestId 请求ID
     */
    public void removeIdempotentMark(String requestId) {
        String key = IDEMPOTENT_KEY_PREFIX + requestId;
        redisTemplate.delete(key);
    }
}