package io.github.lizhifuabc.redis.cache.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.github.lizhifuabc.redis.cache.model.User;
import io.github.lizhifuabc.redis.cache.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Write-Behind（异步写入）策略服务
 * 
 * 特点：
 * 1. 读取数据时，先查缓存，缓存命中则直接返回；缓存未命中则查询数据库，并将结果放入缓存
 * 2. 更新数据时，先更新缓存，然后异步更新数据库
 * 
 * 适用场景：
 * 1. 写入频繁但可以接受短暂不一致的场景
 * 2. 需要提高写入性能的场景
 *
 * @author lizhifu
 * @since 2025/3/13
 */
@Service
public class WriteBehindService {
    
    private static final Logger log = LoggerFactory.getLogger(WriteBehindService.class);
    
    /**
     * 缓存键前缀
     */
    private static final String CACHE_KEY_PREFIX = "write_behind:user:";
    
    /**
     * 缓存过期时间（秒）
     */
    private static final long CACHE_EXPIRE_SECONDS = 3600;
    
    /**
     * 写操作队列，存储待写入数据库的操作
     */
    private final Queue<User> writeQueue = new ConcurrentLinkedQueue<>();
    
    /**
     * 删除操作映射，存储待删除的用户ID
     */
    private final Map<Long, Boolean> deleteMap = new ConcurrentHashMap<>();
    
    private final UserRepository userRepository;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    
    public WriteBehindService(UserRepository userRepository, StringRedisTemplate redisTemplate) {
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
     * 先更新缓存，然后将更新操作放入队列，异步更新数据库
     * 
     * @param user 用户对象
     * @return 更新后的用户对象
     */
    public User updateUser(User user) {
        // 1. 先更新缓存
        String cacheKey = CACHE_KEY_PREFIX + user.getId();
        try {
            String json = objectMapper.writeValueAsString(user);
            redisTemplate.opsForValue().set(cacheKey, json, Duration.ofSeconds(CACHE_EXPIRE_SECONDS));
            log.info("用户数据已更新到缓存，ID: {}", user.getId());
        } catch (JsonProcessingException e) {
            log.error("序列化用户数据失败", e);
        }
        
        // 2. 将更新操作放入队列，等待异步处理
        writeQueue.offer(user);
        log.info("用户更新操作已加入队列，等待异步处理，ID: {}", user.getId());
        
        return user;
    }
    
    /**
     * 删除用户
     * 先从缓存中删除，然后将删除操作放入映射，异步从数据库删除
     * 
     * @param id 用户ID
     */
    public void deleteUser(Long id) {
        // 1. 先从缓存中删除
        String cacheKey = CACHE_KEY_PREFIX + id;
        redisTemplate.delete(cacheKey);
        log.info("用户已从缓存中删除，ID: {}", id);
        
        // 2. 将删除操作放入映射，等待异步处理
        deleteMap.put(id, true);
        log.info("用户删除操作已加入队列，等待异步处理，ID: {}", id);
    }
    
    /**
     * 定时任务，异步处理写入队列中的操作
     * 每5秒执行一次
     */
    @Scheduled(fixedRate = 5000)
    public void processWriteQueue() {
        log.info("开始处理写入队列，当前队列大小: {}", writeQueue.size());
        
        int count = 0;
        User user;
        while ((user = writeQueue.poll()) != null) {
            try {
                userRepository.save(user);
                count++;
                log.info("异步更新用户数据到数据库成功，ID: {}", user.getId());
            } catch (Exception e) {
                log.error("异步更新用户数据到数据库失败，ID: " + user.getId(), e);
                // 失败后重新放入队列，等待下次处理
                writeQueue.offer(user);
            }
        }
        
        log.info("写入队列处理完成，成功处理 {} 条记录", count);
    }
    
    /**
     * 定时任务，异步处理删除映射中的操作
     * 每5秒执行一次
     */
    @Scheduled(fixedRate = 5000)
    public void processDeleteMap() {
        log.info("开始处理删除映射，当前映射大小: {}", deleteMap.size());
        
        int count = 0;
        for (Long id : deleteMap.keySet()) {
            try {
                userRepository.deleteById(id);
                deleteMap.remove(id);
                count++;
                log.info("异步从数据库删除用户成功，ID: {}", id);
            } catch (Exception e) {
                log.error("异步从数据库删除用户失败，ID: " + id, e);
                // 失败后保留在映射中，等待下次处理
            }
        }
        
        log.info("删除映射处理完成，成功处理 {} 条记录", count);
    }
}