package io.github.su322.rabbitmqdemo.rabbitmq;

import io.github.su322.rabbitmqdemo.repository.entity.CommentDO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CommentProducer {
    private static final String EXCHANGE_NAME = "direct_exchange";
    private static final String ROUTING_KEY = "comment";

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendComment(CommentDO commentDO) {
        rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY, commentDO);
    }
}

