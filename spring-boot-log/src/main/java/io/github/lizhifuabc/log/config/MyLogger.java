package io.github.lizhifuabc.log.config;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * 自定义日志格式
 *
 * @author lizhifu
 * @since 2025/3/4
 */
@Slf4j
@Component
public class MyLogger implements CommandLineRunner {
    @Override
    public void run(String... args) throws Exception {
        MDC.put("userId", UUID.randomUUID().toString());
        log.info("hello world==================");
        MDC.remove("userId");
    }
}
