package io.github.lizhifuabc.rbac.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import cn.dev33.satoken.exception.NotPermissionException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.beans.TypeMismatchException;

/**
 * 全局异常处理器
 *
 * @author lizhifu
 * @since 2024/2/24
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理 SpringMVC 参数绑定不正确异常
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BindException.class)
    public Result<?> handleBindException(BindException ex) {
        log.warn("参数绑定异常", ex);
        List<FieldError> fieldErrors = ex.getFieldErrors();
        String message = fieldErrors.stream()
                .map(error -> String.format("%s:%s", error.getField(), error.getDefaultMessage()))
                .collect(Collectors.joining(", "));
        return Result.failed(ResultCode.PARAM_ERROR.getCode(), message);
    }

    /**
     * 处理 SpringMVC 参数校验不正确异常
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.warn("参数校验异常", ex);
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        String message = fieldErrors.stream()
                .map(error -> String.format("%s:%s", error.getField(), error.getDefaultMessage()))
                .collect(Collectors.joining(", "));
        return Result.failed(ResultCode.PARAM_ERROR.getCode(), message);
    }

    /**
     * 处理 SpringMVC 参数类型不正确异常
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public Result<?> handleConstraintViolationException(ConstraintViolationException ex) {
        log.warn("参数类型异常", ex);
        Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
        String message = violations.stream()
                .map(violation -> String.format("%s:%s", violation.getPropertyPath(), violation.getMessage()))
                .collect(Collectors.joining(", "));
        return Result.failed(ResultCode.PARAM_ERROR.getCode(), message);
    }

    /**
     * 处理业务异常
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ServiceException.class)
    public Result<?> handleServiceException(ServiceException ex) {
        log.warn("业务异常", ex);
        return Result.failed(ex.getCode(), ex.getMessage());
    }

    /**
     * 处理系统异常
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception ex) {
        log.error("系统异常", ex);
        return Result.failed(ResultCode.SYSTEM_ERROR.getCode(), "系统异常，请联系管理员");
    }

    /**
     * 处理 JSON 解析异常
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Result<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        log.warn("JSON解析异常", ex);
        return Result.failed(ResultCode.PARAM_ERROR.getCode(), "JSON格式不正确");
    }

    /**
     * 处理类型不匹配异常
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(TypeMismatchException.class)
    public Result<?> handleTypeMismatchException(TypeMismatchException ex) {
        log.warn("参数类型不匹配", ex);
        String propertyName = ex.getPropertyName();
        Class<?> requiredType = ex.getRequiredType();
        String typeName = requiredType != null ? requiredType.getSimpleName() : "未知类型";
        
        return Result.failed(ResultCode.PARAM_ERROR.getCode(), 
            String.format("参数类型不匹配，参数%s需要%s类型", propertyName, typeName));
    }

    /**
     * 处理权限不足异常
     */
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(NotPermissionException.class)
    public Result<?> handleNotPermissionException(NotPermissionException ex) {
        log.warn("权限不足", ex);
        return Result.failed(ResultCode.NO_PERMISSION.getCode(), "没有相关权限");
    }

    /**
     * 处理所有未知异常
     * 注意：这个处理器应该放在最后
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Throwable.class)
    public Result<?> handleThrowable(Throwable ex) {
        log.error("未知系统异常", ex);
        return Result.failed(ResultCode.SYSTEM_ERROR.getCode(), "系统繁忙，请稍后再试");
    }
}