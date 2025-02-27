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

        // 构建Redis键
        String key = buildRedisKey(request, idempotent);

        // 使用Redis的setIfAbsent方法实现幂等性检查
        boolean isFirstRequest = Boolean.TRUE.equals(redisTemplate.opsForValue()
                .setIfAbsent(key, "1", idempotent.timeout(), TimeUnit.SECONDS));

        if (!isFirstRequest) {
            log.warn("重复请求，key: {}", key);
            throw new RepeatRequestException(idempotent.message());
        }

        // 如果配置了自动删除，将key存储到request中，供afterCompletion使用
        if (idempotent.autoDelete()) {
            request.setAttribute("idempotent_key", key);
        }

        return true;
    }

    /**
     * 构建Redis键
     * 优先使用自定义key，如果没有则使用默认生成规则
     */
    private String buildRedisKey(HttpServletRequest request, Idempotent idempotent) {
        // 优先使用请求头中的token
        String token = request.getHeader("Idempotent-Token");
        if (token != null && !token.isEmpty()) {
            return IDEMPOTENT_KEY_PREFIX + token;
        }

        // 其次使用注解中的自定义key
        String key = idempotent.key();
        if (!key.isEmpty()) {
            // TODO: 处理SpEL表达式，可以支持更灵活的key生成规则
            return IDEMPOTENT_KEY_PREFIX + DigestUtils.md5DigestAsHex(key.getBytes());
        }

        // 最后使用默认的key生成规则
        String userId = request.getHeader("User-Id");
        if (userId == null || userId.isEmpty()) {
            throw new RepeatRequestException("无法获取用户标识");
        }

        // 默认规则：用户标识 + 请求URI + 请求方法
        String defaultKey = userId + ":" + request.getRequestURI() + ":" + request.getMethod();
        log.info("使用默认规则生成幂等key：{}", defaultKey);
        
        return IDEMPOTENT_KEY_PREFIX + DigestUtils.md5DigestAsHex(defaultKey.getBytes());
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, 
            Object handler, Exception ex) {
        // 获取需要删除的key
        String key = (String) request.getAttribute("idempotent_key");
        if (key != null) {
            redisTemplate.delete(key);
            log.debug("删除幂等key: {}", key);
        }
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
