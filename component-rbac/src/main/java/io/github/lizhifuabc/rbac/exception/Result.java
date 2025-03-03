package io.github.lizhifuabc.rbac.exception;

import lombok.Data;

/**
 * 统一返回结果
 *
 * @author lizhifu
 * @since 2024/2/24
 */
@Data
public class Result<T> {
    private String code;
    private String message;
    private T data;

    private Result(String code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data);
    }

    public static <T> Result<T> failed(String code, String message) {
        return new Result<>(code, message, null);
    }

    public static <T> Result<T> failed(ResultCode resultCode) {
        return new Result<>(resultCode.getCode(), resultCode.getMessage(), null);
    }
}