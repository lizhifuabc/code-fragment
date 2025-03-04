package io.github.lizhifuabc.tenant.exception;

/**
 * 租户信息缺失异常
 *
 * @author lizhifu
 */
public class MissingTenantException extends RuntimeException {
    public MissingTenantException(String message) {
        super(message);
    }
}