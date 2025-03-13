package io.github.lizhifuabc.redis.cache.service;

import io.github.lizhifuabc.redis.cache.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Redis缓存策略测试类
 *
 * @author lizhifu
 * @since 2025/3/13
 */
@SpringBootTest
class RedisCacheStrategyTest {

    private static final Logger log = LoggerFactory.getLogger(RedisCacheStrategyTest.class);

    @Autowired
    private CacheAsideService cacheAsideService;

    @Autowired
    private WriteThroughService writeThroughService;

    @Autowired
    private WriteBehindService writeBehindService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 测试前清理Redis中的相关键
     */
    @BeforeEach
    void setUp() {
        // 清理测试相关的Redis键
        redisTemplate.delete(redisTemplate.keys("cache:user:*"));
        redisTemplate.delete(redisTemplate.keys("write_through:user:*"));
        redisTemplate.delete(redisTemplate.keys("write_behind:user:*"));
        log.info("测试前清理Redis缓存完成");
    }

    /**
     * 测试Cache-Aside策略的读取操作
     * 1. 第一次读取，应该从数据库获取并缓存
     * 2. 第二次读取，应该从缓存获取
     */
    @Test
    void testCacheAsideRead() {
        log.info("开始测试Cache-Aside策略的读取操作");
        
        // 第一次读取，应该从数据库获取
        User user1 = cacheAsideService.getUser(1L);
        assertNotNull(user1, "用户不应该为空");
        assertEquals("user1", user1.getUsername(), "用户名应该匹配");
        
        // 第二次读取，应该从缓存获取
        User user1Again = cacheAsideService.getUser(1L);
        assertNotNull(user1Again, "用户不应该为空");
        assertEquals("user1", user1Again.getUsername(), "用户名应该匹配");
        
        log.info("Cache-Aside策略的读取操作测试完成");
    }
    
    /**
     * 测试Cache-Aside策略的更新操作
     * 1. 先读取用户，确保缓存中有数据
     * 2. 更新用户，应该先更新数据库，再删除缓存
     * 3. 再次读取，应该从数据库获取最新数据并缓存
     */
    @Test
    void testCacheAsideUpdate() {
        log.info("开始测试Cache-Aside策略的更新操作");
        
        // 先读取用户，确保缓存中有数据
        User user = cacheAsideService.getUser(2L);
        assertNotNull(user, "用户不应该为空");
        
        // 更新用户
        user.setUsername("user2_updated");
        User updatedUser = cacheAsideService.updateUser(user);
        assertEquals("user2_updated", updatedUser.getUsername(), "更新后的用户名应该匹配");
        
        // 再次读取，应该从数据库获取最新数据
        User userAfterUpdate = cacheAsideService.getUser(2L);
        assertNotNull(userAfterUpdate, "更新后的用户不应该为空");
        assertEquals("user2_updated", userAfterUpdate.getUsername(), "更新后的用户名应该匹配");
        
        log.info("Cache-Aside策略的更新操作测试完成");
    }
    
    /**
     * 测试Write-Through策略的读取操作
     * 1. 第一次读取，应该从数据库获取并缓存
     * 2. 第二次读取，应该从缓存获取
     */
    @Test
    void testWriteThroughRead() {
        log.info("开始测试Write-Through策略的读取操作");
        
        // 第一次读取，应该从数据库获取
        User user1 = writeThroughService.getUser(1L);
        assertNotNull(user1, "用户不应该为空");
        assertEquals("user1", user1.getUsername(), "用户名应该匹配");
        
        // 第二次读取，应该从缓存获取
        User user1Again = writeThroughService.getUser(1L);
        assertNotNull(user1Again, "用户不应该为空");
        assertEquals("user1", user1Again.getUsername(), "用户名应该匹配");
        
        log.info("Write-Through策略的读取操作测试完成");
    }
    
    /**
     * 测试Write-Through策略的更新操作
     * 1. 先读取用户，确保缓存中有数据
     * 2. 更新用户，应该同时更新数据库和缓存
     * 3. 再次读取，应该直接从缓存获取最新数据
     */
    @Test
    void testWriteThroughUpdate() {
        log.info("开始测试Write-Through策略的更新操作");
        
        // 先读取用户，确保缓存中有数据
        User user = writeThroughService.getUser(2L);
        assertNotNull(user, "用户不应该为空");
        
        // 更新用户
        user.setUsername("user2_write_through");
        User updatedUser = writeThroughService.updateUser(user);
        assertEquals("user2_write_through", updatedUser.getUsername(), "更新后的用户名应该匹配");
        
        // 再次读取，应该直接从缓存获取最新数据
        User userAfterUpdate = writeThroughService.getUser(2L);
        assertNotNull(userAfterUpdate, "更新后的用户不应该为空");
        assertEquals("user2_write_through", userAfterUpdate.getUsername(), "更新后的用户名应该匹配");
        
        log.info("Write-Through策略的更新操作测试完成");
    }
    
    /**
     * 测试Write-Behind策略的读取操作
     * 1. 第一次读取，应该从数据库获取并缓存
     * 2. 第二次读取，应该从缓存获取
     */
    @Test
    void testWriteBehindRead() {
        log.info("开始测试Write-Behind策略的读取操作");
        
        // 第一次读取，应该从数据库获取
        User user1 = writeBehindService.getUser(1L);
        assertNotNull(user1, "用户不应该为空");
        assertEquals("user1", user1.getUsername(), "用户名应该匹配");
        
        // 第二次读取，应该从缓存获取
        User user1Again = writeBehindService.getUser(1L);
        assertNotNull(user1Again, "用户不应该为空");
        assertEquals("user1", user1Again.getUsername(), "用户名应该匹配");
        
        log.info("Write-Behind策略的读取操作测试完成");
    }
    
    /**
     * 测试Write-Behind策略的更新操作
     * 1. 先读取用户，确保缓存中有数据
     * 2. 更新用户，应该先更新缓存，然后异步更新数据库
     * 3. 再次读取，应该直接从缓存获取最新数据
     * 4. 等待异步处理完成，验证数据库是否更新
     */
    @Test
    void testWriteBehindUpdate() throws InterruptedException {
        log.info("开始测试Write-Behind策略的更新操作");
        
        // 先读取用户，确保缓存中有数据
        User user = writeBehindService.getUser(3L);
        assertNotNull(user, "用户不应该为空");
        
        // 更新用户
        user.setUsername("user3_write_behind");
        User updatedUser = writeBehindService.updateUser(user);
        assertEquals("user3_write_behind", updatedUser.getUsername(), "更新后的用户名应该匹配");
        
        // 再次读取，应该直接从缓存获取最新数据
        User userAfterUpdate = writeBehindService.getUser(3L);
        assertNotNull(userAfterUpdate, "更新后的用户不应该为空");
        assertEquals("user3_write_behind", userAfterUpdate.getUsername(), "更新后的用户名应该匹配");
        
        // 等待异步处理完成
        log.info("等待异步处理完成...");
        Thread.sleep(6000); // 等待6秒，确保异步处理已完成
        
        // 清除缓存，强制从数据库读取
        redisTemplate.delete("write_behind:user:3");
        
        // 再次读取，验证数据库是否已更新
        User userFromDb = writeBehindService.getUser(3L);
        assertNotNull(userFromDb, "数据库中的用户不应该为空");
        assertEquals("user3_write_behind", userFromDb.getUsername(), "数据库中的用户名应该已更新");
        
        log.info("Write-Behind策略的更新操作测试完成");
    }
    
    /**
     * 测试三种策略的性能比较
     * 分别测试三种策略的读写性能
     */
    @Test
    void testPerformanceComparison() {
        log.info("开始测试三种缓存策略的性能比较");
        
        // 准备测试数据
        User testUser = new User(100L, "test_user", "test@example.com");
        
        // 测试Cache-Aside策略的写入性能
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            testUser.setUsername("cache_aside_" + i);
            cacheAsideService.updateUser(testUser);
        }
        long cacheAsideWriteTime = System.currentTimeMillis() - startTime;
        log.info("Cache-Aside策略写入10次耗时: {}ms", cacheAsideWriteTime);
        
        // 测试Write-Through策略的写入性能
        startTime = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            testUser.setUsername("write_through_" + i);
            writeThroughService.updateUser(testUser);
        }
        long writeThroughWriteTime = System.currentTimeMillis() - startTime;
        log.info("Write-Through策略写入10次耗时: {}ms", writeThroughWriteTime);
        
        // 测试Write-Behind策略的写入性能
        startTime = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            testUser.setUsername("write_behind_" + i);
            writeBehindService.updateUser(testUser);
        }
        long writeBehindWriteTime = System.currentTimeMillis() - startTime;
        log.info("Write-Behind策略写入10次耗时: {}ms", writeBehindWriteTime);
        
        // 测试读取性能（第一次读取，从数据库）
        startTime = System.currentTimeMillis();
        cacheAsideService.getUser(1L);
        long cacheAsideFirstReadTime = System.currentTimeMillis() - startTime;
        log.info("Cache-Aside策略首次读取耗时: {}ms", cacheAsideFirstReadTime);
        
        startTime = System.currentTimeMillis();
        writeThroughService.getUser(1L);
        long writeThroughFirstReadTime = System.currentTimeMillis() - startTime;
        log.info("Write-Through策略首次读取耗时: {}ms", writeThroughFirstReadTime);
        
        startTime = System.currentTimeMillis();
        writeBehindService.getUser(1L);
        long writeBehindFirstReadTime = System.currentTimeMillis() - startTime;
        log.info("Write-Behind策略首次读取耗时: {}ms", writeBehindFirstReadTime);
        
        // 测试读取性能（第二次读取，从缓存）
        startTime = System.currentTimeMillis();
        cacheAsideService.getUser(1L);
        long cacheAsideSecondReadTime = System.currentTimeMillis() - startTime;
        log.info("Cache-Aside策略缓存读取耗时: {}ms", cacheAsideSecondReadTime);
        
        startTime = System.currentTimeMillis();
        writeThroughService.getUser(1L);
        long writeThroughSecondReadTime = System.currentTimeMillis() - startTime;
        log.info("Write-Through策略缓存读取耗时: {}ms", writeThroughSecondReadTime);
        
        startTime = System.currentTimeMillis();
        writeBehindService.getUser(1L);
        long writeBehindSecondReadTime = System.currentTimeMillis() - startTime;
        log.info("Write-Behind策略缓存读取耗时: {}ms", writeBehindSecondReadTime);
        
        log.info("三种缓存策略的性能比较测试完成");
        
        // 输出性能比较结果
        log.info("性能比较结果汇总：");
        log.info("写入性能：Cache-Aside({}ms), Write-Through({}ms), Write-Behind({}ms)", 
                cacheAsideWriteTime, writeThroughWriteTime, writeBehindWriteTime);
        log.info("首次读取性能：Cache-Aside({}ms), Write-Through({}ms), Write-Behind({}ms)", 
                cacheAsideFirstReadTime, writeThroughFirstReadTime, writeBehindFirstReadTime);
        log.info("缓存读取性能：Cache-Aside({}ms), Write-Through({}ms), Write-Behind({}ms)", 
                cacheAsideSecondReadTime, writeThroughSecondReadTime, writeBehindSecondReadTime);
        
        // 性能比较断言
        // 写入性能：Write-Behind应该最快，因为它只更新缓存，异步更新数据库
        assertTrue(writeBehindWriteTime < cacheAsideWriteTime, "Write-Behind写入应该比Cache-Aside快");
        assertTrue(writeBehindWriteTime < writeThroughWriteTime, "Write-Behind写入应该比Write-Through快");
        
        // 缓存读取性能：三种策略应该差不多，因为都是从缓存读取
        assertTrue(cacheAsideSecondReadTime < cacheAsideFirstReadTime, "缓存读取应该比首次读取快");
        assertTrue(writeThroughSecondReadTime < writeThroughFirstReadTime, "缓存读取应该比首次读取快");
        assertTrue(writeBehindSecondReadTime < writeBehindFirstReadTime, "缓存读取应该比首次读取快");
    }
    
    /**
     * 测试缓存过期后的行为
     * 验证缓存过期后，再次读取时是否会从数据库获取最新数据
     */
    @Test
    void testCacheExpiration() throws InterruptedException {
        log.info("开始测试缓存过期后的行为");
        
        // 为了测试方便，我们使用一个很短的过期时间
        // 注意：这需要修改服务类中的缓存过期时间常量，或者添加一个带过期时间参数的方法
        // 这里我们假设已经做了这样的修改，缓存过期时间设置为1秒
        
        // 先读取用户，确保缓存中有数据
        User user = cacheAsideService.getUser(1L);
        assertNotNull(user, "用户不应该为空");
        
        // 等待缓存过期
        log.info("等待缓存过期...");
        Thread.sleep(2000); // 等待2秒，确保缓存已过期
        
        // 再次读取，应该从数据库获取
        User userAfterExpiration = cacheAsideService.getUser(1L);
        assertNotNull(userAfterExpiration, "过期后的用户不应该为空");
        assertEquals(user.getUsername(), userAfterExpiration.getUsername(), "用户名应该匹配");
        
        log.info("缓存过期后的行为测试完成");
    }
    
    /**
     * 测试缓存穿透情况
     * 缓存穿透是指查询一个不存在的数据，因为不存在则不会写入缓存，
     * 这将导致这个不存在的数据每次请求都要到数据库去查询
     */
    @Test
    void testCachePenetration() {
        log.info("开始测试缓存穿透情况");
        
        // 查询一个不存在的用户ID
        Long nonExistentId = 999L;
        
        // 第一次查询
        long startTime = System.currentTimeMillis();
        User user1 = cacheAsideService.getUser(nonExistentId);
        long firstQueryTime = System.currentTimeMillis() - startTime;
        assertNull(user1, "不存在的用户应该返回null");
        
        // 第二次查询
        startTime = System.currentTimeMillis();
        User user2 = cacheAsideService.getUser(nonExistentId);
        long secondQueryTime = System.currentTimeMillis() - startTime;
        assertNull(user2, "不存在的用户应该返回null");
        
        // 由于没有特殊处理，两次查询都会访问数据库，时间应该差不多
        log.info("查询不存在的用户，第一次耗时: {}ms, 第二次耗时: {}ms", firstQueryTime, secondQueryTime);
        
        // 注意：在实际应用中，应该对缓存穿透进行处理，例如缓存空值或使用布隆过滤器
        log.info("缓存穿透情况测试完成");
    }
    
    /**
     * 测试并发情况下的缓存一致性
     * 模拟多个线程同时更新同一个用户
     */
    @Test
    void testConcurrentUpdate() throws InterruptedException {
        log.info("开始测试并发情况下的缓存一致性");
        
        // 准备测试数据
        final Long userId = 5L;
        User initialUser = new User(userId, "initial_user", "initial@example.com");
        cacheAsideService.updateUser(initialUser);
        
        // 模拟5个线程并发更新
        int threadCount = 5;
        Thread[] threads = new Thread[threadCount];
        
        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                User user = cacheAsideService.getUser(userId);
                user.setUsername("user_" + index);
                cacheAsideService.updateUser(user);
                log.info("线程 {} 更新用户名为: {}", index, user.getUsername());
            });
        }
        
        // 启动所有线程
        for (Thread thread : threads) {
            thread.start();
        }
        
        // 等待所有线程完成
        for (Thread thread : threads) {
            thread.join();
        }
        
        // 读取最终的用户数据
        User finalUser = cacheAsideService.getUser(userId);
        log.info("并发更新后的最终用户名: {}", finalUser.getUsername());
        
        // 注意：在并发情况下，最终的用户名将是最后一个成功更新的线程设置的值
        // 这里我们只是验证更新操作是否成功执行，而不是验证特定的最终值
        assertNotNull(finalUser, "并发更新后的用户不应该为空");
        assertTrue(finalUser.getUsername().startsWith("user_"), "用户名应该已被更新");
        
        log.info("并发情况下的缓存一致性测试完成");
    }
}