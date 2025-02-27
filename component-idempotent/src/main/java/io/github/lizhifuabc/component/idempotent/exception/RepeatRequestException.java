package io.github.lizhifuabc.component.idempotent.exception;

import java.io.Serial;

/**
 * 重复请求异常
 * 用于幂等性校验时，发现重复请求时抛出
 *
 * @author lizhifu
 * @since 2025/2/26
 */
public class RepeatRequestException extends RuntimeException {
    
    @Serial
    private static final long serialVersionUID = 1L;

    public RepeatRequestException(String message) {
        super(message);
    }

    public RepeatRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
