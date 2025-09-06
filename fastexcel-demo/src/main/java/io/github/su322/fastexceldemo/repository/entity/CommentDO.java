package io.github.su322.fastexceldemo.repository.entity;

import cn.idev.excel.annotation.ExcelProperty;
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
    @ExcelProperty("id")
    private Long id;

    /** 用户ID */
    @ExcelProperty("userId")
    private Long userId;

    /** 评论内容 */
    @ExcelProperty("content")
    private String content;

    /** 评论时间 */
    @ExcelProperty("createTime")
    private LocalDateTime createTime;
}
