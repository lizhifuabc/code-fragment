package io.github.lizhifuabc.component.idempotent.interceptor;

import io.github.lizhifuabc.component.idempotent.annotation.Idempotent;
import io.github.lizhifuabc.component.idempotent.exception.RepeatRequestException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.DigestUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * 幂等拦截器
 *
 * @author lizhifu
 * @since 2025/2/26
 */
@Slf4j
public class IdempotentInterceptor implements HandlerInterceptor {
    
    private final StringRedisTemplate redisTemplate;
    private static final String IDEMPOTENT_KEY_PREFIX = "idempotent:";
    
    public IdempotentInterceptor(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        Method method = handlerMethod.getMethod();
        
        // 获取幂等注解
        Idempotent idempotent = method.getAnnotation(Idempotent.class);
        if (idempotent == null) {
            return true;
        }

        // 获取幂等标识：优先从请求头获取，如果没有则自动生成
        String token = getOrGenerateToken(request);

        // 构建Redis键
        String key = IDEMPOTENT_KEY_PREFIX + token;

        // 使用Redis的setIfAbsent方法实现幂等性检查
        boolean isFirstRequest = Boolean.TRUE.equals(redisTemplate.opsForValue()
                .setIfAbsent(key, "1", idempotent.timeout(), TimeUnit.SECONDS));

        if (!isFirstRequest) {
            log.warn("重复请求，token: {}", token);
            throw new RepeatRequestException(idempotent.message());
        }

        return true;
    }

    /**
     * 获取或生成幂等性标识
     * 优先从请求头获取，如果没有则根据请求信息自动生成
     *
     * @param request HTTP请求
     * @return 幂等性标识
     */
    private String getOrGenerateToken(HttpServletRequest request) {
        // 优先从请求头获取
        String token = request.getHeader("Idempotent-Token");
        if (token != null && !token.isEmpty()) {
            return token;
        }

        // 获取用户标识（根据实际项目的用户认证方式获取）
        String userId = request.getHeader("User-Id"); // 示例，实际应从你的用户认证信息中获取
        if (userId == null || userId.isEmpty()) {
            throw new RepeatRequestException("无法获取用户标识");
        }

        // 自动生成token：用户标识 + 请求URI + 请求方法
        String tokenBuilder = userId +
                ":" +
                request.getRequestURI() +
                ":" +
                request.getMethod();
        
        log.info("生成幂等性标识：{}", tokenBuilder);
        return DigestUtils.md5DigestAsHex(tokenBuilder.getBytes());
    }
}
