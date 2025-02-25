package io.github.lizhifuabc.virtualthreads.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.embedded.tomcat.TomcatProtocolHandlerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.support.TaskExecutorAdapter;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Executors;

/**
 * 虚拟线程配置类
 * <p>
 * 该配置类用于设置 Spring Boot 应用中的虚拟线程支持
 * <p>
 * 1. 配置异步任务执行器使用虚拟线程
 * <p>
 * 2. 配置 Tomcat 使用虚拟线程处理请求
 *
 * @author lizhifu
 * @since 2025/2/25
 */
@EnableAsync // 启用 Spring 异步执行功能
@Configuration // 标识这是一个配置类
@ConditionalOnProperty(name = "spring.threads.virtual.enabled", havingValue = "true") // 仅当配置文件中启用虚拟线程时才生效
public class ThreadConfig {

    /**
     * 配置异步任务执行器
     * <p>
     * 将 Spring 的异步任务执行器配置为使用虚拟线程
     * <p>
     * 用于处理 @Async 注解标记的方法
     *
     * @return AsyncTaskExecutor 异步任务执行器
     */
    @Bean
    public AsyncTaskExecutor applicationTaskExecutor() {
        return new TaskExecutorAdapter(Executors.newVirtualThreadPerTaskExecutor());
    }

    /**
     * 配置 Tomcat 协议处理器
     * <p>
     * 使 Tomcat 服务器使用虚拟线程来处理传入的 HTTP 请求
     *
     * @return TomcatProtocolHandlerCustomizer Tomcat 协议处理器定制器
     */
    @Bean
    public TomcatProtocolHandlerCustomizer<?> protocolHandlerCustomizer() {
        return protocolHandler -> {
            protocolHandler.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
        };
    }
}
