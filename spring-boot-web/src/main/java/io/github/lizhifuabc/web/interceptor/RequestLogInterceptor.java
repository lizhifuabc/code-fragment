package io.github.lizhifuabc.web.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.Enumeration;
import java.util.UUID;

/**
 * 请求日志拦截器
 * 用于记录API接口的请求日志，包括请求URL、参数、响应时间等
 *
 * @author lizhifu
 * @since 2025/3/19
 */
@Component
public class RequestLogInterceptor implements HandlerInterceptor {
    
    private static final Logger logger = LoggerFactory.getLogger(RequestLogInterceptor.class);
    
    /**
     * TraceId的MDC键名
     */
    private static final String TRACE_ID = "traceId";
    
    /**
     * 请求开始时间的请求属性名
     */
    private static final String START_TIME = "requestStartTime";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 生成并设置TraceId
        String traceId = UUID.randomUUID().toString().replace("-", "");
        MDC.put(TRACE_ID, traceId);
        response.setHeader(TRACE_ID, traceId);
        
        // 记录请求开始时间
        request.setAttribute(START_TIME, System.currentTimeMillis());
        
        // 记录请求信息
        logRequest(request);
        
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        // 不需要实现
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 计算请求处理时间
        Long startTime = (Long) request.getAttribute(START_TIME);
        long duration = System.currentTimeMillis() - startTime;
        
        // 记录响应信息
        logResponse(request, response, duration);
        
        // 清除TraceId
        MDC.remove(TRACE_ID);
    }

    /**
     * 记录请求信息
     *
     * @param request HTTP请求
     */
    private void logRequest(HttpServletRequest request) {
        StringBuilder logBuilder = new StringBuilder();
        logBuilder.append("\n请求信息：\n");
        logBuilder.append("TraceId: ").append(MDC.get(TRACE_ID)).append("\n");
        logBuilder.append("请求URL: ").append(request.getRequestURL()).append("\n");
        logBuilder.append("请求方法: ").append(request.getMethod()).append("\n");
        logBuilder.append("客户端IP: ").append(getClientIp(request)).append("\n");
        
        // 记录请求头
        logBuilder.append("请求头: \n");
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            logBuilder.append("  ").append(headerName).append(": ").append(request.getHeader(headerName)).append("\n");
        }
        
        // 记录请求参数
        logBuilder.append("请求参数: \n");
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();
            logBuilder.append("  ").append(paramName).append(": ").append(request.getParameter(paramName)).append("\n");
        }
        
        logger.info(logBuilder.toString());
    }

    /**
     * 记录响应信息
     *
     * @param request HTTP请求
     * @param response HTTP响应
     * @param duration 请求处理时间（毫秒）
     */
    private void logResponse(HttpServletRequest request, HttpServletResponse response, long duration) {
        StringBuilder logBuilder = new StringBuilder();
        logBuilder.append("\n响应信息：\n");
        logBuilder.append("TraceId: ").append(MDC.get(TRACE_ID)).append("\n");
        logBuilder.append("请求URL: ").append(request.getRequestURL()).append("\n");
        logBuilder.append("响应状态码: ").append(response.getStatus()).append("\n");
        logBuilder.append("处理时间: ").append(duration).append("ms\n");
        
        logger.info(logBuilder.toString());
    }

    /**
     * 获取客户端真实IP
     *
     * @param request HTTP请求
     * @return 客户端IP
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        
        // 多个代理的情况，第一个IP为客户端真实IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        
        return ip;
    }
}