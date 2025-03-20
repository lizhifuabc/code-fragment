package io.github.lizhifuabc.web.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * 枚举值校验注解
 * 用于校验字段值是否为指定枚举类中的有效值
 *
 * @author lizhifu
 * @since 2025/3/19
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = EnumValueValidator.class)
public @interface EnumValue {
    
    /**
     * 错误消息
     */
    String message() default "必须为有效的枚举值";
    
    /**
     * 分组
     */
    Class<?>[] groups() default {};
    
    /**
     * 负载
     */
    Class<? extends Payload>[] payload() default {};
    
    /**
     * 枚举类
     */
    Class<? extends Enum<?>> enumClass();
    
    /**
     * 枚举值方法名，默认为name()
     */
    String method() default "name";
}