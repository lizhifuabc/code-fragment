package io.github.lizhifuabc.web.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * 异步服务
 * 用于处理需要异步执行的业务逻辑
 *
 * @author lizhifu
 * @since 2025/3/19
 */
@Service
public class AsyncService {
    
    private static final Logger logger = LoggerFactory.getLogger(AsyncService.class);

    /**
     * 异步处理任务
     *
     * @param taskId 任务ID
     * @param data 任务数据
     * @return 异步结果
     */
    @Async("asyncTaskExecutor")
    public CompletableFuture<String> processTask(String taskId, String data) {
        logger.info("开始异步处理任务: {}", taskId);
        
        try {
            // 模拟耗时操作
            Thread.sleep(3000);
            
            // 处理业务逻辑
            String result = "任务 " + taskId + " 处理完成，数据: " + data;
            logger.info(result);
            
            return CompletableFuture.completedFuture(result);
        } catch (InterruptedException e) {
            logger.error("任务处理异常", e);
            Thread.currentThread().interrupt();
            return CompletableFuture.failedFuture(e);
        }
    }

    /**
     * 异步处理批量任务
     *
     * @param batchId 批次ID
     * @param count 任务数量
     * @return 异步结果
     */
    @Async("asyncTaskExecutor")
    public CompletableFuture<String> processBatchTask(String batchId, int count) {
        logger.info("开始异步处理批量任务: {}, 数量: {}", batchId, count);
        
        try {
            // 模拟批量处理
            for (int i = 0; i < count; i++) {
                logger.info("处理批次 {} 中的第 {} 个任务", batchId, i + 1);
                // 模拟单个任务处理
                Thread.sleep(500);
            }
            
            String result = "批次 " + batchId + " 的 " + count + " 个任务全部处理完成";
            logger.info(result);
            
            return CompletableFuture.completedFuture(result);
        } catch (InterruptedException e) {
            logger.error("批量任务处理异常", e);
            Thread.currentThread().interrupt();
            return CompletableFuture.failedFuture(e);
        }
    }
}