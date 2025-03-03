package io.github.lizhifuabc.redis.local;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.experimental.UtilityClass;

import java.util.concurrent.TimeUnit;

/**
 * Guava Cache 工厂类
 *
 * @author lizhifu
 */
@UtilityClass
public class LocalGuavaCacheFactory {
    /** 默认初始容量 */
    /** 
     * 默认初始容量
     * 用于设置缓存的初始大小，避免在容量不足时频繁扩容
     * 太小会导致频繁扩容，太大会浪费内存
     */
    private static final int DEFAULT_INITIAL_CAPACITY = 200;
    
    /** 
     * 默认并发级别
     * 用于控制内部分段锁的数量，类似 ConcurrentHashMap 的分段锁机制
     * 并发级别越高，并发写入的性能越好，但会占用更多的内存
     * 建议设置为预计并发写入线程数的大约两倍
     * 默认值 5 意味着可以同时支持 5 个线程并发写入
     */
    private static final int DEFAULT_CONCURRENCY_LEVEL = 5;
    /** 默认过期时间（秒） */
    private static final long DEFAULT_EXPIRE_DURATION = 300;

    /**
     * 获取默认配置的本地缓存
     */
    public static <K, V> Cache<K, V> getLocalCache() {
        return getLocalCache(DEFAULT_INITIAL_CAPACITY, DEFAULT_EXPIRE_DURATION);
    }

    /**
     * 获取指定过期时间的本地缓存
     *
     * @param duration 过期时间（秒）
     */
    public static <K, V> Cache<K, V> getLocalCache(long duration) {
        return getLocalCache(DEFAULT_INITIAL_CAPACITY, duration);
    }

    /**
     * 获取指定初始容量和过期时间的本地缓存
     *
     * @param initialCapacity 初始容量
     * @param duration 过期时间（秒）
     */
    public static <K, V> Cache<K, V> getLocalCache(int initialCapacity, long duration) {
        return CacheBuilder.newBuilder()
                .initialCapacity(initialCapacity)
                .concurrencyLevel(DEFAULT_CONCURRENCY_LEVEL)
                .expireAfterWrite(duration, TimeUnit.SECONDS)
                .recordStats()
                .build();
    }
}