package io.github.lizhifuabc.web.exception;

import io.github.lizhifuabc.web.common.ResultCode;

/**
 * 业务异常
 * 用于表示业务逻辑错误，不需要记录堆栈信息
 *
 * @author lizhifu
 * @since 2025/3/19
 */
public class BusinessException extends RuntimeException {
    
    /**
     * 错误码
     */
    private final Integer code;

    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
    }

    public BusinessException(ResultCode resultCode, String message) {
        super(message);
        this.code = resultCode.getCode();
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}