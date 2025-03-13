package io.github.lizhifuabc.redis.id;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * 全局 ID 生成器
 *
 * @author lizhifu
 * @since 2025/3/13
 */
@Component
public class RedisIdGenService {
    // 开始时间戳
    private static final long BEGIN_TIMESTAMP = 1640995200L;

    // 序列号的位数
    private static final int COUNT_BITS = 32;
    
    // 序列号最大值 (2^32)-1
    private static final long MAX_COUNT = (1L << COUNT_BITS) - 1;

    private final StringRedisTemplate stringRedisTemplate;

    public RedisIdGenService(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    // 获取下一个自动生成的 id
    public long nextId(String keyPrefix){
        // 1.生成时间戳
        LocalDateTime now = LocalDateTime.now();
        long nowSecond = now.toEpochSecond(ZoneOffset.UTC);
        long timestamp = nowSecond - BEGIN_TIMESTAMP;

        // 2.获取当前日期
        String date = now.format(DateTimeFormatter.ofPattern("yyyy:MM:dd"));
        
        // 3.获取自增长值：生成一个递增计数值
        long count = stringRedisTemplate.opsForValue().increment("icr:" + keyPrefix + ":" + date);
        
        // 4.检查序列号是否溢出
        if (count > MAX_COUNT) {
            throw new RuntimeException("序列号超出上限，请联系管理员处理");
        }
        
        // 5.拼接并返回
        return timestamp << COUNT_BITS | count;
    }
}
