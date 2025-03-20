package io.github.lizhifuabc.web.config;

import io.github.lizhifuabc.web.interceptor.RequestLogInterceptor;
import io.github.lizhifuabc.web.version.ApiVersionRequestMappingHandlerMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * Web MVC 配置
 * 用于配置拦截器、参数解析器等
 *
 * @author lizhifu
 * @since 2025/3/19
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    
    private final RequestLogInterceptor requestLogInterceptor;

    @Autowired
    public WebMvcConfig(RequestLogInterceptor requestLogInterceptor) {
        this.requestLogInterceptor = requestLogInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册请求日志拦截器
        registry.addInterceptor(requestLogInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/error");
    }

    /**
     * 注册API版本控制处理器
     * 使用不同的Bean名称并添加@Primary注解
     */
    @Bean
    @Primary
    public RequestMappingHandlerMapping apiVersionRequestMappingHandlerMapping() {
        return new ApiVersionRequestMappingHandlerMapping();
    }
}