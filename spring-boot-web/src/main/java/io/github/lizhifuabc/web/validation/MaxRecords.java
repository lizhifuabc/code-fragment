package io.github.lizhifuabc.web.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * 最大记录数限制注解
 * 用于限制批量接口的记录数量
 *
 * @author lizhifu
 * @since 2025/3/19
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = MaxRecordsValidator.class)
public @interface MaxRecords {
    
    /**
     * 错误消息
     */
    String message() default "记录数超过限制";
    
    /**
     * 分组
     */
    Class<?>[] groups() default {};
    
    /**
     * 负载
     */
    Class<? extends Payload>[] payload() default {};
    
    /**
     * 最大记录数
     */
    int value() default 500;
}