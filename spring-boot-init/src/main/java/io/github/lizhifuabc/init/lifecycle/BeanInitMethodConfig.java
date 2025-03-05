package io.github.lizhifuabc.init.lifecycle;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Bean初始化方法配置类
 * 演示使用@Bean注解的initMethod属性指定初始化方法
 *
 * @author lizhifu
 * @since 2024/3/12
 */
@Configuration
public class BeanInitMethodConfig {
    @Bean(initMethod = "initMethod")
    public BeanInitMethodExample beanInitMethodExample() {
        return new BeanInitMethodExample();
    }
}