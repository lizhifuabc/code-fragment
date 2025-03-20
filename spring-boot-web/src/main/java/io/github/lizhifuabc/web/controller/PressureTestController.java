package io.github.lizhifuabc.web.controller;

import io.github.lizhifuabc.web.common.Result;
import io.github.lizhifuabc.web.util.PressureTestUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * 压测控制器
 * 用于对API接口进行压力测试
 *
 * @author lizhifu
 * @since 2025/3/19
 */
@RestController
@RequestMapping("/test/pressure")
public class PressureTestController {
    
    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * 执行压测
     *
     * @param url 测试URL
     * @param concurrency 并发数
     * @param totalRequests 总请求数
     * @return 测试结果
     */
    @GetMapping("/run")
    public Result<String> runPressureTest(
            @RequestParam String url,
            @RequestParam(defaultValue = "10") int concurrency,
            @RequestParam(defaultValue = "1000") int totalRequests) {
        
        // 启动新线程执行压测，避免阻塞当前请求
        new Thread(() -> {
            PressureTestUtil.runTest(concurrency, totalRequests, () -> {
                return restTemplate.getForObject(url, String.class);
            });
        }).start();
        
        return Result.success("压测已启动，请查看日志获取结果");
    }
}