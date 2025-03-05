package io.github.lizhifuabc.init.lifecycle;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 使用@PostConstruct注解的初始化方式
 * 在依赖注入完成后自动调用
 * 执行顺序：构造方法 -> 依赖注入 -> @PostConstruct方法
 *
 * @author lizhifu
 * @since 2024/3/12
 */
@Slf4j
@Component
public class PostConstructBean {
    public PostConstructBean() {
        log.info("[2.1][PostConstructBean] 构造方法执行");
    }

    @PostConstruct
    public void init() {
        log.info("[2.2][PostConstructBean] @PostConstruct方法执行，开始初始化...");
        // 在这里执行初始化逻辑
        log.info("[2.3][PostConstructBean] 初始化完成");
    }
}