package io.github.lizhifuabc.tenant.config;

import io.github.lizhifuabc.tenant.interceptor.TenantInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring MVC 配置类
 * 用于配置多租户拦截器，实现多租户上下文的自动注入
 *
 * @author lizhifu
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * 添加多租户拦截器
     * 拦截所有请求，从请求头中获取租户信息并设置到 TenantContext 中
     *
     * @param registry 拦截器注册表
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new TenantInterceptor());
    }
}