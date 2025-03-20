package io.github.lizhifuabc.web.security;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 限流器
 * 用于限制API接口的访问频率，防止接口被恶意调用
 *
 * @author lizhifu
 * @since 2025/3/19
 */
@Component
public class RateLimiter {

    /**
     * IP限流计数器，记录每个IP的访问次数
     * key: IP地址
     * value: 访问计数器
     */
    private final Map<String, Counter> ipCounters = new ConcurrentHashMap<>();
    
    /**
     * 接口限流计数器，记录每个IP对特定接口的访问次数
     * key: IP地址 + 接口路径
     * value: 访问计数器
     */
    private final Map<String, Counter> apiCounters = new ConcurrentHashMap<>();
    
    /**
     * 用户限流计数器，记录每个用户的访问次数
     * key: 用户标识（如AK）
     * value: 访问计数器
     */
    private final Map<String, Counter> userCounters = new ConcurrentHashMap<>();
    
    /**
     * IP总请求限制（每分钟）
     */
    private final int ipLimit = 10000;
    
    /**
     * 接口请求限制（每分钟）
     */
    private final int apiLimit = 2000;
    
    /**
     * 用户请求限制（每分钟）
     */
    private final int userLimit = 10000;

    /**
     * 检查IP是否超过限流阈值
     *
     * @param ip 请求IP
     * @return 是否允许访问
     */
    public boolean isIpAllowed(String ip) {
        Counter counter = ipCounters.computeIfAbsent(ip, k -> new Counter());
        return counter.incrementAndGet() <= ipLimit;
    }

    /**
     * 检查IP对特定接口是否超过限流阈值
     *
     * @param ip 请求IP
     * @param apiPath 接口路径
     * @return 是否允许访问
     */
    public boolean isApiAllowed(String ip, String apiPath) {
        String key = ip + ":" + apiPath;
        Counter counter = apiCounters.computeIfAbsent(key, k -> new Counter());
        return counter.incrementAndGet() <= apiLimit;
    }

    /**
     * 检查用户是否超过限流阈值
     *
     * @param userId 用户标识（如AK）
     * @return 是否允许访问
     */
    public boolean isUserAllowed(String userId) {
        Counter counter = userCounters.computeIfAbsent(userId, k -> new Counter());
        return counter.incrementAndGet() <= userLimit;
    }

    /**
     * 计数器内部类，用于记录访问次数并自动重置
     */
    private static class Counter {
        /**
         * 访问计数
         */
        private final AtomicInteger count = new AtomicInteger(0);
        
        /**
         * 上次重置时间
         */
        private long lastResetTime = System.currentTimeMillis();

        /**
         * 增加计数并返回当前值
         * 如果距离上次重置已经超过1分钟，则重置计数器
         *
         * @return 当前计数值
         */
        public int incrementAndGet() {
            long now = System.currentTimeMillis();
            if (now - lastResetTime > 60000) { // 1分钟
                count.set(0);
                lastResetTime = now;
            }
            return count.incrementAndGet();
        }
    }
}