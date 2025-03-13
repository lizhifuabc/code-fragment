package io.github.lizhifuabc.redis.cache.repository;

import io.github.lizhifuabc.redis.cache.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用户数据访问层
 * 模拟数据库操作
 *
 * @author lizhifu
 * @since 2025/3/13
 */
@Repository
public class UserRepository {
    
    private static final Logger log = LoggerFactory.getLogger(UserRepository.class);
    
    /**
     * 模拟数据库存储
     */
    private final Map<Long, User> userStore = new ConcurrentHashMap<>();
    
    /**
     * 构造函数，初始化一些测试数据
     */
    public UserRepository() {
        // 初始化一些测试数据
        userStore.put(1L, new User(1L, "user1", "user1@example.com"));
        userStore.put(2L, new User(2L, "user2", "user2@example.com"));
        userStore.put(3L, new User(3L, "user3", "user3@example.com"));
        log.info("UserRepository初始化完成，预置了3条用户数据");
    }
    
    /**
     * 根据ID查询用户
     * 模拟数据库查询操作
     *
     * @param id 用户ID
     * @return 用户对象，如果不存在返回null
     */
    public User findById(Long id) {
        // 模拟数据库查询延迟
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        log.info("从数据库中查询用户，ID: {}", id);
        return userStore.get(id);
    }
    
    /**
     * 保存用户
     * 模拟数据库保存操作
     *
     * @param user 用户对象
     * @return 保存后的用户对象
     */
    public User save(User user) {
        // 模拟数据库写入延迟
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        if (user.getId() == null) {
            // 模拟自增ID
            user.setId((long) (userStore.size() + 1));
        }
        
        if (user.getCreateTime() == null) {
            user.setCreateTime(LocalDateTime.now());
        }
        
        user.setUpdateTime(LocalDateTime.now());
        userStore.put(user.getId(), user);
        log.info("用户数据保存到数据库，ID: {}", user.getId());
        
        return user;
    }
    
    /**
     * 删除用户
     * 模拟数据库删除操作
     *
     * @param id 用户ID
     */
    public void deleteById(Long id) {
        // 模拟数据库删除延迟
        try {
            Thread.sleep(150);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        userStore.remove(id);
        log.info("从数据库中删除用户，ID: {}", id);
    }
}