package io.github.lizhifuabc.component.idempotent.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Idempotent {
    /**
     * 幂等超时时间，单位秒，默认 5 秒
     */
    long timeout() default 5;
    
    /**
     * 提示消息
     */
    String message() default "请勿重复请求";

    /**
     * 自定义 key，支持 SpEL 表达式
     * 为空时使用默认生成规则：userId:uri:method
     */
    String key() default "";

    /**
     * 请求完成后是否自动删除 key
     * true: 请求完成后立即删除
     * false: 等待超时时间后自动删除
     */
    boolean autoDelete() default false;
}
