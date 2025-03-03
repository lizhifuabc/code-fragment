package io.github.lizhifuabc.redis.local;

import com.google.common.cache.Cache;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Guava Cache 工具类
 *
 * @author lizhifu
 */
@Component
@Slf4j
public class GuavaCache<K, V> {
    private Cache<K, V> cache;

    @PostConstruct
    public void init() {
        cache = LocalGuavaCacheFactory.getLocalCache();
    }
    
    /**
     * 将数据放入缓存
     */
    public void put(K key, V value) {
        if (key != null && value != null) {
            cache.put(key, value);
            log.debug("GuavaCache put key={}", key);
        }
    }

    /**
     * 从缓存获取数据
     */
    public Optional<V> getIfPresent(K key) {
        if (key == null) {
            return Optional.empty();
        }
        V value = cache.getIfPresent(key);
        if (value != null) {
            log.debug("GuavaCache hit key={}", key);
        }
        return Optional.ofNullable(value);
    }

    /**
     * 从缓存中移除数据
     */
    public void remove(K key) {
        if (key != null) {
            cache.invalidate(key);
            log.debug("GuavaCache remove key={}", key);
        }
    }

    /**
     * 清空缓存
     */
    public void clear() {
        cache.invalidateAll();
        log.debug("GuavaCache clear all");
    }

    /**
     * 获取缓存统计信息
     */
    public String stats() {
        return cache.stats().toString();
    }
}