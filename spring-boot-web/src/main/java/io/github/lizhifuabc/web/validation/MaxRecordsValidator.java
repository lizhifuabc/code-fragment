package io.github.lizhifuabc.web.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Collection;

/**
 * 最大记录数限制校验器
 * 用于校验批量接口的记录数量是否超过限制
 *
 * @author lizhifu
 * @since 2025/3/19
 */
public class MaxRecordsValidator implements ConstraintValidator<MaxRecords, Collection<?>> {
    
    /**
     * 最大记录数
     */
    private int maxRecords;

    @Override
    public void initialize(MaxRecords constraintAnnotation) {
        this.maxRecords = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(Collection<?> value, ConstraintValidatorContext context) {
        // 如果值为null，不进行校验，交给@NotNull处理
        if (value == null) {
            return true;
        }
        
        return value.size() <= maxRecords;
    }
}