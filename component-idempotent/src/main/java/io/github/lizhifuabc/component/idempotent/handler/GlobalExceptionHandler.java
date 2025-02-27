package io.github.lizhifuabc.component.idempotent.handler;

import io.github.lizhifuabc.component.idempotent.exception.RepeatRequestException;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 *
 * @author lizhifu
 * @since 2025/2/26
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RepeatRequestException.class)
    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    public Result handleRepeatRequestException(RepeatRequestException e) {
        return Result.error(HttpStatus.TOO_MANY_REQUESTS.value(), e.getMessage());
    }

    @Data
    public static class Result {
        private Integer code;
        private String message;
        private Object data;

        public static Result error(Integer code, String message) {
            Result result = new Result();
            result.setCode(code);
            result.setMessage(message);
            return result;
        }
    }
}