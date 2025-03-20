package io.github.lizhifuabc.web.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * 枚举值校验器
 * 用于校验字段值是否为指定枚举类中的有效值
 *
 * @author lizhifu
 * @since 2025/3/19
 */
public class EnumValueValidator implements ConstraintValidator<EnumValue, Object> {
    
    /**
     * 枚举值集合
     */
    private final Set<Object> values = new HashSet<>();

    @Override
    public void initialize(EnumValue enumValue) {
        Class<? extends Enum<?>> enumClass = enumValue.enumClass();
        String methodName = enumValue.method();
        
        try {
            Method method = enumClass.getMethod(methodName);
            Enum<?>[] enums = enumClass.getEnumConstants();
            
            for (Enum<?> enumConstant : enums) {
                values.add(method.invoke(enumConstant));
            }
        } catch (Exception e) {
            throw new RuntimeException("枚举值校验初始化失败", e);
        }
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        // 如果值为null，不进行校验，交给@NotNull处理
        if (value == null) {
            return true;
        }
        
        return values.contains(value);
    }
}