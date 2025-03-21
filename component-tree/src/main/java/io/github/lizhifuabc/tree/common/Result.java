package io.github.lizhifuabc.tree.common;

import lombok.Data;

/**
 * 通用响应结果
 *
 * @author lizhifu
 * @since 2023/7/1
 */
@Data
public class Result<T> {
    private static final int SUCCESS_CODE = 200;
    private static final int ERROR_CODE = 500;
    private static final String SUCCESS_MESSAGE = "操作成功";
    private static final String ERROR_MESSAGE = "操作失败";

    /**
     * 状态码
     */
    private int code;

    /**
     * 消息
     */
    private String message;

    /**
     * 数据
     */
    private T data;

    /**
     * 成功结果
     *
     * @return 成功结果
     */
    public static <T> Result<T> success() {
        return success(null);
    }

    /**
     * 成功结果
     *
     * @param data 数据
     * @return 成功结果
     */
    public static <T> Result<T> success(T data) {
        return success(SUCCESS_MESSAGE, data);
    }

    /**
     * 成功结果
     *
     * @param message 消息
     * @param data    数据
     * @return 成功结果
     */
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(SUCCESS_CODE, message, data);
    }

    /**
     * 错误结果
     *
     * @return 错误结果
     */
    public static <T> Result<T> error() {
        return error(ERROR_MESSAGE);
    }

    /**
     * 错误结果
     *
     * @param message 消息
     * @return 错误结果
     */
    public static <T> Result<T> error(String message) {
        return error(ERROR_CODE, message);
    }

    /**
     * 错误结果
     *
     * @param code    状态码
     * @param message 消息
     * @return 错误结果
     */
    public static <T> Result<T> error(int code, String message) {
        return new Result<>(code, message, null);
    }

    /**
     * 构造函数
     *
     * @param code    状态码
     * @param message 消息
     * @param data    数据
     */
    private Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
}