package io.github.lizhifuabc.redis.id;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * RedisIdGenService 测试类
 *
 * @author lizhifu
 * @since 2025/3/13
 */
@SpringBootTest
class RedisIdGenServiceTest {
    
    private static final Logger log = LoggerFactory.getLogger(RedisIdGenServiceTest.class);

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    
    @Autowired
    private RedisIdGenService redisIdGenService;
    
    private final String TEST_KEY_PREFIX = "test:id:gen";
    
    @BeforeEach
    void setUp() {
        // 清理测试相关的Redis键
        Set<String> keys = stringRedisTemplate.keys("icr:" + TEST_KEY_PREFIX + ":*");
        if (!keys.isEmpty()) {
            log.info("清理测试键: {}", keys);
            stringRedisTemplate.delete(keys);
        }
    }

    @Test
    void testNextId() {
        log.info("开始测试ID生成基本功能");
        // 生成ID
        long id1 = redisIdGenService.nextId(TEST_KEY_PREFIX);
        long id2 = redisIdGenService.nextId(TEST_KEY_PREFIX);
        
        log.info("生成的ID: id1={}, id2={}", id1, id2);
        
        // 验证ID递增
        assertTrue(id2 > id1, "第二个ID应该大于第一个ID");
        
        // 验证ID结构 (时间戳部分应该相同，序列号部分应该不同)
        long timestamp1 = id1 >> 32;
        long timestamp2 = id2 >> 32;
        long sequence1 = id1 & 0xFFFFFFFFL;
        long sequence2 = id2 & 0xFFFFFFFFL;
        
        log.info("ID1解析: 时间戳={}, 序列号={}", timestamp1, sequence1);
        log.info("ID2解析: 时间戳={}, 序列号={}", timestamp2, sequence2);
        
        assertEquals(timestamp1, timestamp2, "短时间内生成的ID时间戳部分应该相同");
        assertEquals(1, sequence1, "第一个序列号应该是1");
        assertEquals(2, sequence2, "第二个序列号应该是2");
        
        log.info("ID生成基本功能测试通过");
    }
    
    @Test
    void testIdUniqueness() throws InterruptedException {
        // 测试并发情况下ID的唯一性
        int threadCount = 20;
        int idsPerThread = 50;
        log.info("开始测试ID唯一性，线程数={}, 每线程ID数={}", threadCount, idsPerThread);
        
        Set<Long> ids = new HashSet<>(threadCount * idsPerThread);
        CountDownLatch latch = new CountDownLatch(threadCount);
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < threadCount; i++) {
            final int threadIndex = i;
            executorService.submit(() -> {
                try {
                    for (int j = 0; j < idsPerThread; j++) {
                        long id = redisIdGenService.nextId(TEST_KEY_PREFIX);
                        synchronized (ids) {
                            ids.add(id);
                        }
                    }
                    log.debug("线程 {} 完成ID生成", threadIndex);
                } finally {
                    latch.countDown();
                }
            });
        }
        
        latch.await();
        executorService.shutdown();
        
        long endTime = System.currentTimeMillis();
        log.info("ID生成完成，耗时={}ms，预期ID数={}, 实际ID数={}", 
                (endTime - startTime), threadCount * idsPerThread, ids.size());
        
        assertEquals(threadCount * idsPerThread, ids.size(), "所有生成的ID应该是唯一的");
        log.info("ID唯一性测试通过");
    }
    
    @Test
    void testDifferentKeyPrefix() {
        log.info("开始测试不同前缀的ID生成");
        // 测试不同前缀生成的ID序列号是独立的
        String prefix1 = TEST_KEY_PREFIX + ":one";
        String prefix2 = TEST_KEY_PREFIX + ":two";
        
        long id1 = redisIdGenService.nextId(prefix1);
        long id2 = redisIdGenService.nextId(prefix1);
        long id3 = redisIdGenService.nextId(prefix2);
        
        log.info("prefix1第一个ID={}", id1);
        log.info("prefix1第二个ID={}", id2);
        log.info("prefix2第一个ID={}", id3);
        
        long sequence1 = id1 & 0xFFFFFFFFL;
        long sequence2 = id2 & 0xFFFFFFFFL;
        long sequence3 = id3 & 0xFFFFFFFFL;
        
        log.info("序列号解析: sequence1={}, sequence2={}, sequence3={}", sequence1, sequence2, sequence3);
        
        assertEquals(1, sequence1, "prefix1的第一个序列号应该是1");
        assertEquals(2, sequence2, "prefix1的第二个序列号应该是2");
        assertEquals(1, sequence3, "prefix2的第一个序列号应该是1");
        
        log.info("不同前缀ID生成测试通过");
    }
}