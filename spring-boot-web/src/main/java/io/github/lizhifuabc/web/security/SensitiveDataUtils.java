package io.github.lizhifuabc.web.security;

import org.springframework.util.StringUtils;

/**
 * 敏感数据工具类
 * 用于对敏感数据进行脱敏处理
 *
 * @author lizhifu
 * @since 2025/3/19
 */
public class SensitiveDataUtils {

    /**
     * 手机号脱敏
     * 保留前3位和后4位，中间用*代替
     *
     * @param mobile 手机号
     * @return 脱敏后的手机号
     */
    public static String maskMobile(String mobile) {
        if (!StringUtils.hasText(mobile)) {
            return mobile;
        }
        
        if (mobile.length() <= 7) {
            return mobile;
        }
        
        return mobile.substring(0, 3) + "****" + mobile.substring(mobile.length() - 4);
    }

    /**
     * 银行卡号脱敏
     * 保留前6位和后4位，中间用*代替
     *
     * @param cardNo 银行卡号
     * @return 脱敏后的银行卡号
     */
    public static String maskBankCard(String cardNo) {
        if (!StringUtils.hasText(cardNo)) {
            return cardNo;
        }
        
        if (cardNo.length() <= 10) {
            return cardNo;
        }
        
        return cardNo.substring(0, 6) + "******" + cardNo.substring(cardNo.length() - 4);
    }

    /**
     * 身份证号脱敏
     * 保留前6位和后4位，中间用*代替
     *
     * @param idCard 身份证号
     * @return 脱敏后的身份证号
     */
    public static String maskIdCard(String idCard) {
        if (!StringUtils.hasText(idCard)) {
            return idCard;
        }
        
        if (idCard.length() <= 10) {
            return idCard;
        }
        
        return idCard.substring(0, 6) + "********" + idCard.substring(idCard.length() - 4);
    }

    /**
     * 姓名脱敏
     * 只显示第一个字符，其他用*代替
     *
     * @param name 姓名
     * @return 脱敏后的姓名
     */
    public static String maskName(String name) {
        if (!StringUtils.hasText(name)) {
            return name;
        }
        
        if (name.length() == 1) {
            return name;
        }
        
        if (name.length() == 2) {
            return name.substring(0, 1) + "*";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append(name.charAt(0));
        for (int i = 0; i < name.length() - 2; i++) {
            sb.append("*");
        }
        sb.append(name.charAt(name.length() - 1));
        
        return sb.toString();
    }

    /**
     * 邮箱脱敏
     * 邮箱前缀仅显示第一个字符，其他用*代替，@及后面的地址显示
     *
     * @param email 邮箱
     * @return 脱敏后的邮箱
     */
    public static String maskEmail(String email) {
        if (!StringUtils.hasText(email)) {
            return email;
        }
        
        int atIndex = email.indexOf('@');
        if (atIndex <= 1) {
            return email;
        }
        
        String prefix = email.substring(0, atIndex);
        String suffix = email.substring(atIndex);
        
        if (prefix.length() <= 1) {
            return prefix + suffix;
        }
        
        return prefix.charAt(0) + "****" + suffix;
    }

    /**
     * 地址脱敏
     * 只显示到地区，不显示详细地址
     *
     * @param address 地址
     * @param sensitiveLength 敏感信息长度
     * @return 脱敏后的地址
     */
    public static String maskAddress(String address, int sensitiveLength) {
        if (!StringUtils.hasText(address) || address.length() <= sensitiveLength) {
            return address;
        }
        
        return address.substring(0, address.length() - sensitiveLength) + "****";
    }
}