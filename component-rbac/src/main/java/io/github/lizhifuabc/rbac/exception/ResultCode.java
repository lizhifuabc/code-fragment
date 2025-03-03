package io.github.lizhifuabc.rbac.exception;

import lombok.Getter;

/**
 * 返回码枚举
 *
 * @author lizhifu
 * @since 2024/2/24
 */
@Getter
public enum ResultCode {
    SUCCESS("00000", "成功"),
    PARAM_ERROR("A0400", "请求参数错误"),
    NO_PERMISSION("A0301", "没有访问权限"),
    SYSTEM_ERROR("B0001", "系统执行出错");

    private final String code;
    private final String message;

    ResultCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}