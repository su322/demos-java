package io.github.su322.rabbitmqdemo.repository.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("comment")
public class CommentDO {
    /** 主键ID */
    private Long id;

    /** 用户ID */
    private Long userId;

    /** 评论内容 */
    private String content;

    /** 评论时间 */
    private LocalDateTime createTime;
}
