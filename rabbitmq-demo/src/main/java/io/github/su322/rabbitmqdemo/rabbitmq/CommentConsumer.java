package io.github.su322.rabbitmqdemo.rabbitmq;

import io.github.su322.rabbitmqdemo.repository.mapper.CommentMapper;
import io.github.su322.rabbitmqdemo.repository.entity.CommentDO;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CommentConsumer {
    @Autowired
    private CommentMapper commentMapper;

    @RabbitListener(queues = "comment_queue")
    public void receiveComment(CommentDO commentDO) {
        commentMapper.insert(commentDO);
    }
}
