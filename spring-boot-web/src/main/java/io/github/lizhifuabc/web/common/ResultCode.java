package io.github.lizhifuabc.web.common;

/**
 * 响应状态码
 *
 * @author lizhifu
 * @since 2025/3/19
 */
public enum ResultCode {
    /**
     * 成功
     */
    SUCCESS(0, "成功"),
    /**
     * 参数错误
     */
    PARAM_ERROR(1001, "参数错误"),
    /**
     * 签名错误
     */
    SIGN_ERROR(1002, "签名错误"),
    /**
     * 签名过期
     */
    SIGN_EXPIRED(1003, "签名已过期"),
    /**
     * IP不在白名单
     */
    IP_NOT_ALLOWED(1004, "IP不在白名单"),
    /**
     * 请求频率超限
     */
    RATE_LIMIT_EXCEEDED(1005, "请求频率超限"),
    /**
     * 记录数超限
     */
    RECORDS_LIMIT_EXCEEDED(1006, "记录数超限"),
    /**
     * 无访问权限
     */
    NO_PERMISSION(1007, "无访问权限"),
    /**
     * 服务器内部错误
     */
    INTERNAL_SERVER_ERROR(500, "服务器内部错误");

    private final Integer code;
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}