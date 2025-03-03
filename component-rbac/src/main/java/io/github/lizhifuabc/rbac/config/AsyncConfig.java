package io.github.lizhifuabc.rbac.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.core.task.AsyncTaskExecutor;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 异步任务线程池配置
 * 提供两种线程池实现：
 * 1. 平台线程池：适用于CPU密集型任务，如复杂计算、数据处理等
 * 2. 虚拟线程池：适用于IO密集型任务，如数据库操作、远程调用、文件操作等
 *
 * @author lizhifu
 * @since 2024/2/24
 */
@Slf4j
@EnableAsync
@Configuration
public class AsyncConfig {
    
    /**
     * 线程池类型枚举
     */
    public enum ThreadType {
        PLATFORM,    // 平台线程池
        VIRTUAL     // 虚拟线程池
    }

    @Value("${async.thread.type:PLATFORM}")
    private ThreadType threadType;

    /**
     * 线程池配置bean名称
     * 使用示例：
     * 1. @Async("rbac-platform-executor") - 指定使用平台线程池
     * 2. @Async("rbac-virtual-executor") - 指定使用虚拟线程池
     * 3. @Async - 使用默认线程池（由async.thread.type配置决定）
     */
    public static final String PLATFORM_EXECUTOR_NAME = "rbac-platform-executor";
    public static final String VIRTUAL_EXECUTOR_NAME = "rbac-virtual-executor";

    /**
     * 平台线程池配置
     * 适用场景：
     * 1. CPU密集型任务：复杂计算、数据处理
     * 2. 需要精确控制线程数量的场景
     * 3. 对线程资源管理要求较高的场景
     * 配置说明：
     * - 核心线程数：CPU核心数-1，保留一个核心给系统使用
     * - 最大线程数：核心线程数*2，提供弹性伸缩能力
     * - 队列容量：200，防止任务积压
     * - 拒绝策略：CallerRunsPolicy，防止任务丢失
     */
    @Bean(name = PLATFORM_EXECUTOR_NAME)
    public AsyncTaskExecutor platformExecutor() {
        int processors = Runtime.getRuntime().availableProcessors();
        int threadCount = Math.max(1, processors - 1);
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(threadCount);
        taskExecutor.setMaxPoolSize(threadCount * 2);
        taskExecutor.setQueueCapacity(200);
        taskExecutor.setKeepAliveSeconds(60);
        taskExecutor.setThreadNamePrefix(PLATFORM_EXECUTOR_NAME);
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        taskExecutor.initialize();
        return taskExecutor;
    }

    /**
     * 虚拟线程池配置
     * 适用场景：
     * 1. IO密集型任务：数据库操作、远程调用、文件操作
     * 2. 大量并发但单个任务执行时间较短的场景
     * 3. 需要支持海量任务并发执行的场景
     * 特点：
     * - 轻量级：创建成本低，支持百万级并发
     * - 自动调度：不需要手动管理线程数量
     * - JDK 21+：需要Java 21或更高版本支持
     */
    @Bean(name = VIRTUAL_EXECUTOR_NAME)
    public AsyncTaskExecutor virtualExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setVirtualThreads(true);
        taskExecutor.setThreadNamePrefix(VIRTUAL_EXECUTOR_NAME);
        taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        taskExecutor.initialize();
        return taskExecutor;
    }

    /**
     * 默认执行器配置
     * 使用场景：
     * 1. 使用@Async注解但未指定线程池名称时
     * 2. 通过配置文件动态切换线程池类型
     * 配置方式：
     * 在application.properties中设置：
     * async.thread.type=platform 或 
     * async.thread.type=virtual
     */
    @Primary
    @Bean
    public AsyncTaskExecutor defaultExecutor() {
        return ThreadType.VIRTUAL.equals(threadType) ? virtualExecutor() : platformExecutor();
    }

    /**
     * spring 异步任务 异常配置
     */
    @Configuration
    public static class AsyncExceptionConfig implements AsyncConfigurer {
        @Override
        public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
            return new AsyncExceptionHandler();
        }
    }

    /**
     * 自定义异常处理
     */
    public static class AsyncExceptionHandler implements AsyncUncaughtExceptionHandler {
        @Override
        public void handleUncaughtException(Throwable throwable, Method method, Object... objects) {
            log.error("异步任务发生异常:{}, 参数:{}, ", 
                method.getDeclaringClass().getSimpleName() + "." + method.getName(), 
                Arrays.toString(objects), 
                throwable);
        }
    }
}