package io.github.lizhifuabc.rabbitmq.consumer;

import io.github.lizhifuabc.rabbitmq.config.RabbitMQConfig;
import io.github.lizhifuabc.rabbitmq.model.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import com.rabbitmq.client.Channel;

/**
 * 消息消费者
 *
 * @author lizhifu
 * @since 2024/2/24
 */
@Slf4j
@Component
public class MessageConsumer {

    /**
     * 监听队列，接收消息
     *
     * @param message 消息
     */
    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void receiveMessage(Message message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        log.info("接收到消息: {}", message);
        // 这里处理接收到的消息
        // 根据消息类型进行不同的处理
        switch (message.getType()) {
            case "notification" -> log.info("处理通知消息: {}", message.getContent());
            case "event" -> log.info("处理事件消息: {}", message.getContent());
            default -> log.info("处理未知类型消息: {}", message.getContent());
        }
        
        try {
            // 手动确认消息已被消费
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.error("消息处理失败，但仍然标记为已消费: {}", message, e);
            try {
                // 即使处理失败也确认消息已被消费，避免消息重试
                channel.basicAck(deliveryTag, false);
            } catch (Exception ex) {
                log.error("消息确认失败", ex);
            }
        }
    }
}