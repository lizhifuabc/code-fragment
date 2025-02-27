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
}
