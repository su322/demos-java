package io.github.su322.fastexceldemo.controller;

import io.github.su322.fastexceldemo.service.CommentService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/comment")
public class CommentController {
    @Autowired
    CommentService commentService;

    /**
     * 准备实验导出500万条数据，我把数据准备好，其他参数就不管了
     * @param response
     * @return
     */
    @GetMapping("/download2excel")
    public void download2excel(HttpServletResponse response) throws IOException {
        response.reset();
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("导出数据", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-Disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

        commentService.download2excel(response);
    }

    /**
     * 并发分页查询+串行写入Excel，4线程导出
     */
    @GetMapping("/download2excelConcurrent")
    public void download2excelConcurrent(HttpServletResponse response) throws IOException, InterruptedException {
        response.reset();
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("并发导出数据", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-Disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        commentService.download2excelConcurrent(response);
    }

    /**
     * 虚拟线程并发分页查询+串行写入Excel，JDK21+虚拟线程导出
     */
    @GetMapping("/download2excelVirtualThread")
    public void download2excelVirtualThread(HttpServletResponse response) throws IOException, InterruptedException {
        response.reset();
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("虚拟线程导出数据", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-Disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        commentService.download2excelVirtualThread(response);
    }

    /**
     * 批量插入500万条评论数据，便于测试大数据量导出
     */
    @PostMapping("/batchInsert5Million")
    public String batchInsert5Million() {
        commentService.batchInsert5Million();
        return "插入完成";
    }

    /**
     * 无条件删除 comment 表所有数据
     */
    @PostMapping("/deleteAll")
    public String deleteAllComments() {
        commentService.deleteAllComments();
        return "删除完成";
    }

    /**
     * 查询评论总数
     */
    @GetMapping("/count")
    public long countComments() {
        return commentService.countComments();
    }
}
