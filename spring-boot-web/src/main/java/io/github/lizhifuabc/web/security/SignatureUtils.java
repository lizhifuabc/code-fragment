package io.github.lizhifuabc.web.security;

import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.TreeMap;

/**
 * 签名工具类
 * 用于API接口的签名验证，防止数据被篡改
 *
 * @author lizhifu
 * @since 2025/3/19
 */
public class SignatureUtils {

    /**
     * 签名有效期（毫秒）
     */
    private static final long SIGN_EXPIRE_TIME = 15 * 60 * 1000;

    /**
     * 生成签名
     *
     * @param params 请求参数
     * @param timestamp 时间戳
     * @param secretKey 密钥
     * @return 签名
     */
    public static String generateSign(Map<String, String> params, String timestamp, String secretKey) {
        // 将参数按照字典序排序
        TreeMap<String, String> sortedParams = new TreeMap<>(params);
        StringBuilder sb = new StringBuilder();
        
        // 拼接参数
        for (Map.Entry<String, String> entry : sortedParams.entrySet()) {
            if (StringUtils.hasText(entry.getValue())) {
                sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
        }
        
        // 拼接时间戳和密钥
        sb.append("timestamp=").append(timestamp).append("&");
        sb.append("key=").append(secretKey);
        
        // 使用MD5生成签名
        return DigestUtils.md5DigestAsHex(sb.toString().getBytes()).toUpperCase();
    }

    /**
     * 验证签名
     *
     * @param params 请求参数
     * @param timestamp 时间戳
     * @param sign 签名
     * @param secretKey 密钥
     * @return 是否验证通过
     */
    public static boolean verifySign(Map<String, String> params, String timestamp, String sign, String secretKey) {
        // 验证时间戳是否过期
        if (!verifyTimestamp(timestamp)) {
            return false;
        }
        
        // 生成签名并比较
        String generatedSign = generateSign(params, timestamp, secretKey);
        return generatedSign.equals(sign);
    }

    /**
     * 验证时间戳是否有效
     *
     * @param timestamp 时间戳
     * @return 是否有效
     */
    public static boolean verifyTimestamp(String timestamp) {
        try {
            long requestTime = Long.parseLong(timestamp);
            long currentTime = System.currentTimeMillis();
            
            // 验证时间戳是否在有效期内
            return (currentTime - requestTime) <= SIGN_EXPIRE_TIME;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}