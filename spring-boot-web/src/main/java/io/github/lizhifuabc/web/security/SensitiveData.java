package io.github.lizhifuabc.web.security;

import java.lang.annotation.*;

/**
 * 敏感数据注解
 * 用于标记需要脱敏的字段
 *
 * @author lizhifu
 * @since 2025/3/19
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SensitiveData {
    
    /**
     * 脱敏类型
     */
    SensitiveType type();
    
    /**
     * 脱敏类型枚举
     */
    enum SensitiveType {
        /**
         * 手机号
         */
        MOBILE,
        
        /**
         * 银行卡号
         */
        BANK_CARD,
        
        /**
         * 身份证号
         */
        ID_CARD,
        
        /**
         * 姓名
         */
        NAME,
        
        /**
         * 邮箱
         */
        EMAIL,
        
        /**
         * 地址
         */
        ADDRESS
    }
}