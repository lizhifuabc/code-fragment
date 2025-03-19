package io.github.lizhifuabc.rabbitmq.controller;

import io.github.lizhifuabc.rabbitmq.producer.MessageProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 消息控制器
 *
 * @author lizhifu
 * @since 2024/2/24
 */
@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageProducer messageProducer;

    /**
     * 发送消息
     *
     * @param content 消息内容
     * @param type 消息类型
     * @return 结果
     */
    @PostMapping("/send")
    public String sendMessage(@RequestParam("content") String content,
                            @RequestParam("type") String type) {
        messageProducer.sendMessage(content, type);
        return "消息已发送";
    }
}