package io.github.lizhifuabc.init.lifecycle;

import lombok.extern.slf4j.Slf4j;

/**
 * Bean初始化方法示例类
 * 用于演示@Bean注解的initMethod属性初始化方式
 *
 * @author lizhifu
 * @since 2024/3/12
 */
@Slf4j
public class BeanInitMethodExample {
    public BeanInitMethodExample() {
        log.info("[3.1][BeanInitMethodExample] 构造方法执行");
    }

    public void initMethod() {
        log.info("[3.2][BeanInitMethodExample] initMethod方法执行，开始初始化...");
        // 在这里执行初始化逻辑
        log.info("[3.3][BeanInitMethodExample] 初始化完成");
    }
}