package io.github.lizhifuabc.rbac.exception;

import lombok.Getter;

/**
 * 业务异常
 *
 * @author lizhifu
 * @since 2024/2/24
 */
@Getter
public class ServiceException extends RuntimeException {
    private final String code;

    public ServiceException(String code, String message) {
        super(message);
        this.code = code;
    }

    public ServiceException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
    }
}