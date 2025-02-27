package io.github.lizhifuabc.component.idempotent.controller;

import io.github.lizhifuabc.component.idempotent.annotation.Idempotent;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 幂等测试控制器
 *
 * @author lizhifu
 * @since 2025/2/26
 */
@Slf4j
@RestController
@RequestMapping("/idempotent")
public class IdempotentTestController {

    @Idempotent(timeout = 5, message = "请勿重复提交订单")
    @PostMapping("/order")
    public OrderResponse createOrder(@RequestBody OrderRequest request) {
        log.info("收到创建订单请求：{}", request);
        // 模拟业务处理
        OrderResponse response = new OrderResponse();
        response.setOrderId("ORDER_" + System.currentTimeMillis());
        response.setMessage("订单创建成功");
        return response;
    }

    @Data
    public static class OrderRequest {
        private String productId;
        private Integer quantity;
        private String userId;
    }

    @Data
    public static class OrderResponse {
        private String orderId;
        private String message;
    }
}