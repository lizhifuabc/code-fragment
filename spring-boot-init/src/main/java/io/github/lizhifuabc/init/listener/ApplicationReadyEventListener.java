package io.github.lizhifuabc.init.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * ApplicationReadyEvent事件监听器
 * 在应用程序准备就绪时执行初始化操作
 * 这个事件在所有CommandLineRunner和ApplicationRunner执行完成后触发
 *
 * @author lizhifu
 * @since 2024/3/12
 */
@Slf4j
@Component
public class ApplicationReadyEventListener implements ApplicationListener<ApplicationReadyEvent> {
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        log.info("[ApplicationReadyEventListener] 应用程序已就绪，开始执行初始化操作...");
        // 在这里执行初始化逻辑
        log.info("[ApplicationReadyEventListener] 初始化操作完成");
    }
}