package io.github.lizhifuabc.redis.utils;

import com.google.common.hash.Funnels;
import com.google.common.hash.Hashing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * Redis BitMap 工具类
 * 
 * <h2>BitMap 介绍</h2>
 * Redis 的 BitMap 是一种特殊的数据结构，本质上是字符串，但可以对字符串的位进行操作。
 * 可以将 BitMap 理解为一个以位为单位的数组，数组的每个单元只能存储 0 或 1，数组的下标在 BitMap 中称为偏移量。
 * 
 * <h2>主要特点</h2>
 * <ul>
 *     <li>存储空间优化：单个 BitMap 最大支持 512MB，可存储 2^32 个比特位</li>
 *     <li>高性能：位操作的时间复杂度为 O(1)</li>
 *     <li>适用场景：适合存储大量的布尔类型数据，如用户签到、在线状态等</li>
 * </ul>
 * 
 * <h2>操作类型</h2>
 * <ul>
 *     <li>单点操作：对特定位进行设置或获取</li>
 *     <li>批量操作：范围统计、位运算等</li>
 * </ul>
 * 
 * <h2>使用限制</h2>
 * 由于位值只能为 0 或 1，因此仅适用于布尔类型数据的存储和统计
 *
 * @author lizhifu
 * @since 2025/3/3
 */
@Component
public class RedisBitMapUtils {
    private static StringRedisTemplate stringRedisTemplate;

    /**
     * 计算 Hash 值
     *
     * @param key bitmap结构的key
     * @return Hash 值
     */
    private static long hash(String key) {
        return Math.abs(Hashing.murmur3_128().hashObject(key, Funnels.stringFunnel(StandardCharsets.UTF_8)).asInt());
    }

    /**
     * 设置与 param 对应的二进制位的值，{@param param}会经过hash计算进行存储。
     *
     * @param key   bitmap数据结构的key
     * @param param 要设置偏移的key，该key会经过hash运算。
     * @param value true：即该位设置为1，否则设置为0
     * @return 返回设置该value之前的值。
     */
    public static Boolean setBit(String key, String param, boolean value) {
        return stringRedisTemplate.opsForValue().setBit(key, hash(param), value);
    }

    /**
     * 查询与指定 param 对应二进制位的值，{@param param}会经过hash计算进行存储。
     *
     * @param key   bitmap结构的key
     * @param param 要移除偏移的key，该key会经过hash运算。
     * @return 若偏移位上的值为1，那么返回true。
     */
    public static boolean getBit(String key, String param) {
        return Boolean.TRUE.equals(stringRedisTemplate.opsForValue().getBit(key, hash(param)));
    }

    /**
     * 将指定offset偏移量的值设置为1；
     *
     * @param key    bitmap结构的key
     * @param offset 指定的偏移量。
     * @param value  true：即该位设置为1，否则设置为0
     * @return 返回设置该value之前的值。
     */
    public static Boolean setBit(String key, Long offset, boolean value) {
        return stringRedisTemplate.opsForValue().setBit(key, offset, value);
    }

    /**
     * 获取指定 offset 偏移量的值；
     *
     * @param key    bitmap结构的key
     * @param offset 指定的偏移量。
     * @return 若偏移位上的值为 1，那么返回true。
     */
    public static Boolean getBit(String key, long offset) {
        return stringRedisTemplate.opsForValue().getBit(key, offset);
    }

    /**
     * 统计对应的bitmap上value为1的数量
     *
     * @param key bitmap的key
     * @return value等于1的数量
     */
    public static Long bitCount(String key) {
        return stringRedisTemplate.execute((RedisCallback<Long>) connection -> connection.stringCommands().bitCount(key.getBytes(StandardCharsets.UTF_8)));
    }

    /**
     * 统计指定范围中value为1的数量
     *
     * @param key   bitMap中的key
     * @param start 该参数的单位是byte（1byte=8bit），{@code setBit(key,7,true);}进行存储时，单位是bit。那么只需要统计[0,1]便可以统计到上述set的值。
     * @param end   该参数的单位是byte。
     * @return 在指定范围[start*8,end*8]内所有value=1的数量
     */
    public static Long bitCount(String key, int start, int end) {
        return stringRedisTemplate.execute((RedisCallback<Long>) connection -> connection.stringCommands().bitCount(key.getBytes(), start, end));
    }

    /**
     * 对一个或多个保存二进制的字符串key进行元操作，并将结果保存到saveKey上。
     * <p>
     * bitop and saveKey key [key...]，对一个或多个key逻辑并，结果保存到saveKey。
     * bitop or saveKey key [key...]，对一个或多个key逻辑或，结果保存到saveKey。
     * bitop xor saveKey key [key...]，对一个或多个key逻辑异或，结果保存到saveKey。
     * bitop xor saveKey key，对一个或多个key逻辑非，结果保存到saveKey。
     * <p>
     *
     * @param op      元操作类型；
     * @param saveKey 元操作后将结果保存到saveKey所在的结构中。
     * @param destKey 需要进行元操作的类型。
     * @return 1：返回元操作值。
     */
    public static Long bitOp(RedisStringCommands.BitOperation op, String saveKey, String... destKey) {
        byte[][] bytes = new byte[destKey.length][];
        for (int i = 0; i < destKey.length; i++) {
            bytes[i] = destKey[i].getBytes();
        }
        return stringRedisTemplate.execute((RedisCallback<Long>) connection -> connection.stringCommands().bitOp(op, saveKey.getBytes(), bytes));
    }

    /**
     * 对一个或多个保存二进制的字符串key进行元操作，并将结果保存到saveKey上，并返回统计之后的结果。
     *
     * @param op      元操作类型；
     * @param saveKey 元操作后将结果保存到saveKey所在的结构中。
     * @param destKey 需要进行元操作的类型。
     * @return 返回saveKey结构上value=1的所有数量值。
     */
    public static Long bitOpResult(RedisStringCommands.BitOperation op, String saveKey, String... destKey) {
        bitOp(op, saveKey, destKey);
        return bitCount(saveKey);
    }

    @Autowired
    @Qualifier(value = "stringRedisTemplate")
    public void setStringRedisTemplate(StringRedisTemplate stringRedisTemplate) {
        RedisBitMapUtils.stringRedisTemplate = stringRedisTemplate;
    }
}
