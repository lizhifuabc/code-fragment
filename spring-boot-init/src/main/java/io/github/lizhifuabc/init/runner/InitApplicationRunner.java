package io.github.lizhifuabc.init.runner;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * ApplicationRunner接口实现类
 * 与CommandLineRunner类似，但提供了更结构化的参数访问方式
 * 可以通过@Order注解指定执行顺序，数字越小优先级越高
 *
 * @author lizhifu
 * @since 2024/3/12
 */
@Slf4j
@Component
@Order(2)
public class InitApplicationRunner implements ApplicationRunner {
    @Override
    public void run(ApplicationArguments args) {
        log.info("[InitApplicationRunner] 开始执行初始化操作...");
        // 可以通过args获取应用程序参数
        log.info("[InitApplicationRunner] 获取的参数: {}", args.getOptionNames());
        // 在这里执行初始化逻辑
        log.info("[InitApplicationRunner] 初始化操作完成");
    }
}