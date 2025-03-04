package io.github.lizhifuabc.tenant.context;

/**
 * 租户上下文持有者
 * 用于存储和管理当前线程的租户信息
 *
 * @author lizhifu
 */
public class TenantContextHolder {
    /**
     * 使用 ThreadLocal 存储租户 ID，确保线程隔离
     */
    private static final ThreadLocal<String> TENANT_ID = new ThreadLocal<>();

    /**
     * 设置当前线程的租户 ID
     *
     * @param tenantId 租户 ID
     */
    public static void setTenantId(String tenantId) {
        TENANT_ID.set(tenantId);
    }

    /**
     * 获取当前线程的租户 ID
     *
     * @return 租户 ID
     */
    public static String getTenantId() {
        return TENANT_ID.get();
    }

    /**
     * 清除当前线程的租户 ID
     */
    public static void clear() {
        TENANT_ID.remove();
    }
}