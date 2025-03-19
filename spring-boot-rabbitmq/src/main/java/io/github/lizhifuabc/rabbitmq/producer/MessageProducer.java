package io.github.lizhifuabc.rabbitmq.producer;

import io.github.lizhifuabc.rabbitmq.config.RabbitMQConfig;
import io.github.lizhifuabc.rabbitmq.model.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 消息生产者
 *
 * @author lizhifu
 * @since 2024/2/24
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MessageProducer {

    private final RabbitTemplate rabbitTemplate;

    /**
     * 发送消息
     *
     * @param content 消息内容
     * @param type 消息类型
     */
    public void sendMessage(String content, String type) {
        Message message = Message.builder()
                .id(UUID.randomUUID().toString())
                .content(content)
                .type(type)
                .createTime(LocalDateTime.now())
                .build();
        
        log.info("发送消息: {}", message);
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.ROUTING_KEY, message);
    }
}