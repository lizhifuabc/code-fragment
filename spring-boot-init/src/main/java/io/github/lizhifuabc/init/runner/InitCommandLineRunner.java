package io.github.lizhifuabc.init.runner;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * CommandLineRunner接口实现类
 * 用于在Spring Boot应用程序启动后执行一些初始化操作
 * 可以通过@Order注解指定执行顺序，数字越小优先级越高
 *
 * @author lizhifu
 * @since 2024/3/12
 */
@Slf4j
@Component
@Order(1)
public class InitCommandLineRunner implements CommandLineRunner {
    @Override
    public void run(String... args) {
        log.info("[InitCommandLineRunner] 开始执行初始化操作...");
        // 在这里执行初始化逻辑，比如加载配置、建立连接等
        log.info("[InitCommandLineRunner] 初始化操作完成");
    }
}