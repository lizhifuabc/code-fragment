package io.github.lizhifuabc.web.version;

import java.lang.annotation.*;

/**
 * API版本注解
 * 用于标记API接口的版本
 *
 * @author lizhifu
 * @since 2025/3/19
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiVersion {
    
    /**
     * 版本号
     * 例如：1.0.0
     */
    String value();
}