package io.github.su322.fastexceldemo.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.su322.fastexceldemo.repository.entity.CommentDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommentMapper extends BaseMapper<CommentDO> {
}
