package io.github.lizhifuabc.extension.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 请求包装器
 * 用于读取请求体和响应体内容，可用于日志记录、审计等场景
 *
 * @author lizhifu
 * @since 2025/2/25
 */
@Slf4j
@Component
public class RequestWrapperFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 包装请求和响应
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);
        
        try {
            // 执行过滤链
            filterChain.doFilter(requestWrapper, responseWrapper);
            
            // 获取请求信息
            String requestBody = new String(requestWrapper.getContentAsByteArray(), StandardCharsets.UTF_8);
            String requestURI = request.getRequestURI();
            String method = request.getMethod();
            
            // 获取响应信息
            byte[] responseContent = responseWrapper.getContentAsByteArray();
            String responseBody = new String(responseContent, StandardCharsets.UTF_8);
            
            // 记录请求和响应信息
            if (isJsonRequest(request)) {
                log.info("请求URI: {}, 方法: {}, 请求体: {}, 响应体: {}", 
                    requestURI, method, requestBody, responseBody);
            }
        } finally {
            // 重要：复制响应内容到原始响应
            responseWrapper.copyBodyToResponse();
        }
    }
    
    /**
     * 判断是否为 JSON 请求
     */
    private boolean isJsonRequest(HttpServletRequest request) {
        String contentType = request.getContentType();
        return contentType != null && contentType.contains(MediaType.APPLICATION_JSON_VALUE);
    }
}
