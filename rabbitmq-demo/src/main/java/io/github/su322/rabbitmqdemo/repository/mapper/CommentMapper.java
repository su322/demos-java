package io.github.su322.rabbitmqdemo.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.su322.rabbitmqdemo.repository.entity.CommentDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommentMapper extends BaseMapper<CommentDO> {
    // 继承 BaseMapper 后，自动拥有常用的增删改查方法
}
