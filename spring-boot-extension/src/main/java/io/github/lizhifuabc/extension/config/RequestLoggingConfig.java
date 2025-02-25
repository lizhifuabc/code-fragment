package io.github.lizhifuabc.extension.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

/**
 * 请求日志配置类
 * 用于配置 Spring 的请求日志记录功能，可以记录请求的详细信息
 * 注意：需要在 application.properties 中配置日志级别：
 * logging.level.org.springframework.web.filter.CommonsRequestLoggingFilter=DEBUG
 *
 * @author lizhifu
 * @since 2025/2/25
 */
@Configuration
public class RequestLoggingConfig {
    
    /**
     * 配置请求日志过滤器
     * 该过滤器可以记录请求的详细信息，包括：
     * 1. 查询字符串信息（URL 参数）
     * 2. 请求体信息（POST 的 body 内容）
     * 3. 请求头信息（HTTP headers）
     * 4. 客户端信息（IP 地址等）
     *
     * @return CommonsRequestLoggingFilter 配置好的请求日志过滤器
     */
    @Bean
    public CommonsRequestLoggingFilter logFilter() {
        CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
        // 设置是否记录请求参数（URL 中的查询参数）
        filter.setIncludeQueryString(true);
        // 设置是否记录请求体内容
        filter.setIncludePayload(true);
        // 设置是否记录请求头信息
        filter.setIncludeHeaders(true);
        // 设置是否记录客户端信息（如 IP 地址）
        filter.setIncludeClientInfo(true);
        // 设置最大请求体记录长度，避免记录过大的请求内容
        filter.setMaxPayloadLength(10000);
        // 设置请求日志的前缀，方便日志查找和分析
        filter.setAfterMessagePrefix("REQUEST DATA : ");
        // 设置是否记录请求处理之后的信息
        filter.setAfterMessageSuffix("");
        
        return filter;
    }
}
