package io.github.lizhifuabc.tenant.interceptor;

import io.github.lizhifuabc.tenant.context.TenantContextHolder;
import io.github.lizhifuabc.tenant.exception.MissingTenantException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 多租户拦截器
 * 负责从请求中提取租户信息并设置到上下文中
 *
 * @author lizhifu
 */
public class TenantInterceptor implements HandlerInterceptor {

    /**
     * 租户 ID 的请求头名称
     */
    private static final String TENANT_HEADER = "X-Tenant-ID";

    /**
     * 在请求处理之前执行
     * 从请求头中获取租户 ID 并设置到上下文中
     *
     * @param request 当前 HTTP 请求
     * @param response 当前 HTTP 响应
     * @param handler 选择要执行的处理程序
     * @return true 表示继续处理，false 表示中断处理
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String tenantId = request.getHeader(TENANT_HEADER);
        if (tenantId == null || tenantId.trim().isEmpty()) {
            throw new MissingTenantException("租户ID不能为空，请在请求头中添加 " + TENANT_HEADER);
        }
        TenantContextHolder.setTenantId(tenantId.trim());
        return true;
    }

    /**
     * 在请求完成之后执行
     * 清理租户上下文信息
     *
     * @param request 当前 HTTP 请求
     * @param response 当前 HTTP 响应
     * @param handler 选择要执行的处理程序
     * @param ex 处理过程中抛出的异常
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        TenantContextHolder.clear();
    }
}