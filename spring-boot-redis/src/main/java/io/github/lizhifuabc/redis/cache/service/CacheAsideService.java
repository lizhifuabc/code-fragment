package io.github.lizhifuabc.redis.cache.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.github.lizhifuabc.redis.cache.model.User;
import io.github.lizhifuabc.redis.cache.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * Cache-Aside（旁路缓存）策略服务
 * 
 * 特点：
 * 1. 读取数据时，先查缓存，缓存命中则直接返回；缓存未命中则查询数据库，并将结果放入缓存
 * 2. 更新数据时，先更新数据库，再删除缓存（或更新缓存）
 * 
 * 适用场景：
 * 1. 读多写少的业务场景
 * 2. 对数据一致性要求不是特别高的场景
 *
 * @author lizhifu
 * @since 2025/3/13
 */
@Service
public class CacheAsideService {
    
    private static final Logger log = LoggerFactory.getLogger(CacheAsideService.class);
    
    /**
     * 缓存键前缀
     */
    private static final String CACHE_KEY_PREFIX = "cache:user:";
    
    /**
     * 缓存过期时间（秒）
     */
    private static final long CACHE_EXPIRE_SECONDS = 3600;
    
    private final UserRepository userRepository;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    
    public CacheAsideService(UserRepository userRepository, StringRedisTemplate redisTemplate) {
        this.userRepository = userRepository;
        this.redisTemplate = redisTemplate;
        
        // 配置ObjectMapper以支持Java 8日期时间类型
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }
    
    /**
     * 获取用户信息
     * 
     * @param id 用户ID
     * @return 用户对象，如果不存在返回null
     */
    public User getUser(Long id) {
        String cacheKey = CACHE_KEY_PREFIX + id;
        
        // 1. 先查询缓存
        String userJson = redisTemplate.opsForValue().get(cacheKey);
        
        // 2. 缓存命中，直接返回
        if (userJson != null) {
            log.info("缓存命中，从缓存获取用户，ID: {}", id);
            try {
                return objectMapper.readValue(userJson, User.class);
            } catch (JsonProcessingException e) {
                log.error("解析缓存中的用户数据失败", e);
            }
        }
        
        // 3. 缓存未命中，查询数据库
        log.info("缓存未命中，从数据库获取用户，ID: {}", id);
        User user = userRepository.findById(id);
        
        // 4. 将数据库查询结果放入缓存
        if (user != null) {
            try {
                String json = objectMapper.writeValueAsString(user);
                redisTemplate.opsForValue().set(cacheKey, json, Duration.ofSeconds(CACHE_EXPIRE_SECONDS));
                log.info("用户数据已放入缓存，ID: {}", id);
            } catch (JsonProcessingException e) {
                log.error("序列化用户数据失败", e);
            }
        }
        
        return user;
    }
    
    /**
     * 更新用户信息
     * 
     * @param user 用户对象
     * @return 更新后的用户对象
     */
    public User updateUser(User user) {
        // 1. 先更新数据库
        User updatedUser = userRepository.save(user);
        
        // 2. 删除缓存
        String cacheKey = CACHE_KEY_PREFIX + user.getId();
        redisTemplate.delete(cacheKey);
        log.info("用户数据已更新，缓存已删除，ID: {}", user.getId());
        
        return updatedUser;
    }
    
    /**
     * 删除用户
     * 
     * @param id 用户ID
     */
    public void deleteUser(Long id) {
        // 1. 先删除数据库中的数据
        userRepository.deleteById(id);
        
        // 2. 删除缓存
        String cacheKey = CACHE_KEY_PREFIX + id;
        redisTemplate.delete(cacheKey);
        log.info("用户已删除，缓存已清除，ID: {}", id);
    }
}