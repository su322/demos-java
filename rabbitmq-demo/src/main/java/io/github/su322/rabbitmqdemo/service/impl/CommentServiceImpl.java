package io.github.su322.rabbitmqdemo.service.impl;

import io.github.su322.rabbitmqdemo.repository.entity.CommentDO;
import io.github.su322.rabbitmqdemo.repository.mapper.CommentMapper;
import io.github.su322.rabbitmqdemo.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    private CommentMapper commentMapper;

    @Override
    public CommentDO saveComment(CommentDO commentDO) {
        // 最直接的插入 没有做字段非空检查
        commentMapper.insert(commentDO);
        // 查一次数据库 返回所有字段 否则时间是null
        return commentMapper.selectById(commentDO.getId());
    }
}
