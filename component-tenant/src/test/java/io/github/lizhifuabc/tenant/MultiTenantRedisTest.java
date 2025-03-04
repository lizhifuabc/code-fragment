package io.github.lizhifuabc.tenant;

import io.github.lizhifuabc.tenant.context.TenantContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@Slf4j
@SpringBootTest
public class MultiTenantRedisTest {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    public void testMultiTenantRedis() {
        // 测试租户1：key前缀方案
        TenantContextHolder.setTenantId("tenant1");
        redisTemplate.opsForValue().set("user:1", "张三");
        log.info("租户1数据: {}", redisTemplate.opsForValue().get("user:1"));
        
        // 测试租户2：database方案
        TenantContextHolder.setTenantId("tenant2");
        redisTemplate.opsForValue().set("user:1", "李四");
        log.info("租户2数据: {}", redisTemplate.opsForValue().get("user:1"));
        
        // 测试租户3：独立实例方案
        TenantContextHolder.setTenantId("tenant3");
        redisTemplate.opsForValue().set("user:1", "王五");
        log.info("租户3数据: {}", redisTemplate.opsForValue().get("user:1"));
        
        // 清理测试数据
        TenantContextHolder.setTenantId("tenant1");
        redisTemplate.delete("user:1");
        
        TenantContextHolder.setTenantId("tenant2");
        redisTemplate.delete("user:1");
        
        TenantContextHolder.setTenantId("tenant3");
        redisTemplate.delete("user:1");
    }

    @Test
    public void testDataIsolation() {
        // 测试数据隔离
        TenantContextHolder.setTenantId("tenant1");
        redisTemplate.opsForValue().set("test:key", "tenant1-data");

        TenantContextHolder.setTenantId("tenant2");
        redisTemplate.opsForValue().set("test:key", "tenant2-data");

        // 验证数据隔离
        TenantContextHolder.setTenantId("tenant1");
        log.info("租户1隔离数据: {}", redisTemplate.opsForValue().get("test:key"));

        TenantContextHolder.setTenantId("tenant2");
        log.info("租户2隔离数据: {}", redisTemplate.opsForValue().get("test:key"));

        // 清理测试数据
        TenantContextHolder.setTenantId("tenant1");
        redisTemplate.delete("test:key");
        
        TenantContextHolder.setTenantId("tenant2");
        redisTemplate.delete("test:key");
    }

    @Test
    public void testTenantSwitch() {
        try {
            // 测试租户切换
            TenantContextHolder.setTenantId("tenant1");
            redisTemplate.opsForValue().set("switch:test", "data1");

            TenantContextHolder.setTenantId("tenant2");
            redisTemplate.opsForValue().set("switch:test", "data2");

            // 切回租户1
            TenantContextHolder.setTenantId("tenant1");
            log.info("切换后租户1数据: {}", redisTemplate.opsForValue().get("switch:test"));

            // 清理数据
            redisTemplate.delete("switch:test");
            
            TenantContextHolder.setTenantId("tenant2");
            redisTemplate.delete("switch:test");
        } finally {
            // 确保清理租户上下文
            TenantContextHolder.clear();
        }
    }
}