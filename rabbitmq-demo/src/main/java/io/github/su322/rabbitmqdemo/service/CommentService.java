package io.github.su322.rabbitmqdemo.service;

import io.github.su322.rabbitmqdemo.repository.entity.CommentDO;

public interface CommentService {
    CommentDO saveComment(CommentDO commentDO);
}
