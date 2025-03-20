package io.github.lizhifuabc.web.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 * 压测工具类
 * 用于对API接口进行简单的压力测试
 *
 * @author lizhifu
 * @since 2025/3/19
 */
public class PressureTestUtil {
    
    private static final Logger logger = LoggerFactory.getLogger(PressureTestUtil.class);

    /**
     * 执行压测
     *
     * @param concurrency 并发数
     * @param totalRequests 总请求数
     * @param task 测试任务
     * @param <T> 返回值类型
     */
    public static <T> void runTest(int concurrency, int totalRequests, Supplier<T> task) {
        ExecutorService executorService = Executors.newFixedThreadPool(concurrency);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(totalRequests);
        
        // 统计信息
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);
        AtomicInteger totalTime = new AtomicInteger(0);
        
        // 提交任务
        for (int i = 0; i < totalRequests; i++) {
            executorService.submit(() -> {
                try {
                    // 等待所有线程就绪
                    startLatch.await();
                    
                    long start = System.currentTimeMillis();
                    try {
                        // 执行测试任务
                        T result = task.get();
                        successCount.incrementAndGet();
                    } catch (Exception e) {
                        failCount.incrementAndGet();
                        logger.error("压测任务执行异常", e);
                    }
                    long end = System.currentTimeMillis();
                    
                    // 累计执行时间
                    totalTime.addAndGet((int) (end - start));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    // 任务完成
                    endLatch.countDown();
                }
            });
        }
        
        // 开始计时
        long testStart = System.currentTimeMillis();
        startLatch.countDown();
        
        try {
            // 等待所有任务完成
            endLatch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // 结束计时
        long testEnd = System.currentTimeMillis();
        
        // 关闭线程池
        executorService.shutdown();
        
        // 输出统计信息
        logger.info("压测结果：");
        logger.info("并发数：{}", concurrency);
        logger.info("总请求数：{}", totalRequests);
        logger.info("成功请求数：{}", successCount.get());
        logger.info("失败请求数：{}", failCount.get());
        logger.info("总耗时：{}ms", testEnd - testStart);
        logger.info("平均响应时间：{}ms", totalTime.get() / totalRequests);
        logger.info("QPS：{}", 1000.0 * totalRequests / (testEnd - testStart));
    }
}