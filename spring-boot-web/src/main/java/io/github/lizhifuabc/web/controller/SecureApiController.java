package io.github.lizhifuabc.web.controller;

import io.github.lizhifuabc.web.common.Result;
import io.github.lizhifuabc.web.common.ResultCode;
import io.github.lizhifuabc.web.exception.BusinessException;
import io.github.lizhifuabc.web.idempotent.IdempotentService;
import io.github.lizhifuabc.web.security.IpWhitelistService;
import io.github.lizhifuabc.web.security.RateLimiter;
import io.github.lizhifuabc.web.security.SensitiveDataUtils;
import io.github.lizhifuabc.web.security.SignatureUtils;
import io.github.lizhifuabc.web.service.AsyncService;
import io.github.lizhifuabc.web.validation.MaxRecords;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 安全API控制器
 * 实现了各种API安全和最佳实践功能
 *
 * @author lizhifu
 * @since 2025/3/19
 */
@RestController
@RequestMapping("/api/v1/secure")
@Validated
public class SecureApiController {
    
    private static final Logger logger = LoggerFactory.getLogger(SecureApiController.class);
    
    private final IpWhitelistService ipWhitelistService;
    private final RateLimiter rateLimiter;
    private final IdempotentService idempotentService;
    private final AsyncService asyncService;
    
    // 模拟密钥
    private static final String SECRET_KEY = "your_secret_key";

    @Autowired
    public SecureApiController(
            IpWhitelistService ipWhitelistService,
            RateLimiter rateLimiter,
            IdempotentService idempotentService,
            AsyncService asyncService) {
        this.ipWhitelistService = ipWhitelistService;
        this.rateLimiter = rateLimiter;
        this.idempotentService = idempotentService;
        this.asyncService = asyncService;
    }

    /**
     * 获取用户信息（带签名验证和数据脱敏）
     *
     * @param userId 用户ID
     * @param timestamp 时间戳
     * @param sign 签名
     * @return 用户信息
     */
    @GetMapping("/user")
    public Result<Map<String, Object>> getUserInfo(
            @RequestParam @NotBlank(message = "用户ID不能为空") String userId,
            @RequestParam @NotBlank(message = "时间戳不能为空") String timestamp,
            @RequestParam @NotBlank(message = "签名不能为空") String sign) {
        
        // 获取客户端IP
        String clientIp = getClientIp();
        
        // IP白名单校验
        if (!ipWhitelistService.isIpAllowed(clientIp)) {
            throw new BusinessException(ResultCode.IP_NOT_ALLOWED);
        }
        
        // 限流校验
        if (!rateLimiter.isIpAllowed(clientIp)) {
            throw new BusinessException(ResultCode.RATE_LIMIT_EXCEEDED);
        }
        
        // 签名校验
        Map<String, String> params = new HashMap<>();
        params.put("userId", userId);
        if (!SignatureUtils.verifySign(params, timestamp, sign, SECRET_KEY)) {
            throw new BusinessException(ResultCode.SIGN_ERROR);
        }
        
        // 模拟获取用户信息
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("userId", userId);
        userInfo.put("name", SensitiveDataUtils.maskName("张三"));
        userInfo.put("mobile", SensitiveDataUtils.maskMobile("18888888888"));
        userInfo.put("email", SensitiveDataUtils.maskEmail("zhangsan@example.com"));
        userInfo.put("idCard", SensitiveDataUtils.maskIdCard("310123199001011234"));
        userInfo.put("bankCard", SensitiveDataUtils.maskBankCard("6222021234567890123"));
        userInfo.put("address", SensitiveDataUtils.maskAddress("上海市浦东新区张江高科技园区", 10));
        
        return Result.success(userInfo);
    }

    /**
     * 批量处理数据（带记录数限制和幂等性校验）
     *
     * @param requestId 请求ID
     * @param items 数据项列表
     * @return 处理结果
     */
    @PostMapping("/batch")
    public Result<String> batchProcess(
            @RequestHeader("X-Request-Id") @NotBlank(message = "请求ID不能为空") String requestId,
            @RequestBody @Valid @MaxRecords(value = 500, message = "批量处理数据不能超过500条") List<BatchItem> items) {
        
        // 幂等性校验
        if (idempotentService.isRepeatedRequest(requestId)) {
            logger.info("重复请求，requestId: {}", requestId);
            return Result.success("请求已处理");
        }
        
        // 异步处理批量任务
        CompletableFuture<String> future = asyncService.processBatchTask(requestId, items.size());
        
        // 返回任务已接收
        return Result.success("任务已接收，批次ID: " + requestId);
    }

    /**
     * 查询任务状态
     *
     * @param taskId 任务ID
     * @return 任务状态
     */
    @GetMapping("/task/{taskId}")
    public Result<Map<String, Object>> getTaskStatus(@PathVariable @NotBlank(message = "任务ID不能为空") String taskId) {
        // 模拟查询任务状态
        Map<String, Object> taskStatus = new HashMap<>();
        taskStatus.put("taskId", taskId);
        taskStatus.put("status", "PROCESSING");
        taskStatus.put("progress", 75);
        taskStatus.put("startTime", "2025-03-19 10:30:00");
        taskStatus.put("estimatedEndTime", "2025-03-19 10:35:00");
        
        return Result.success(taskStatus);
    }

    /**
     * 批量数据项
     */
    public static class BatchItem {
        
        @NotBlank(message = "ID不能为空")
        private String id;
        
        @NotBlank(message = "名称不能为空")
        @Size(max = 50, message = "名称长度不能超过50")
        private String name;
        
        private String description;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    /**
     * 获取客户端IP
     *
     * @return 客户端IP
     */
    private String getClientIp() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        
        // 多个代理的情况，第一个IP为客户端真实IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        
        return ip;
    }
}