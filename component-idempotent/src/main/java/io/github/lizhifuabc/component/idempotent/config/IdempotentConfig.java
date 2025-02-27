package io.github.lizhifuabc.component.idempotent.config;

import io.github.lizhifuabc.component.idempotent.interceptor.IdempotentInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class IdempotentConfig implements WebMvcConfigurer {
    
    private final StringRedisTemplate redisTemplate;
    
    public IdempotentConfig(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new IdempotentInterceptor(redisTemplate))
                .addPathPatterns("/**");
    }
}