package io.github.su322.rabbitmqdemo.controller;

import org.springframework.web.bind.annotation.*;
import io.github.su322.rabbitmqdemo.repository.entity.CommentDO;
import io.github.su322.rabbitmqdemo.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import io.github.su322.rabbitmqdemo.rabbitmq.CommentProducer;

@RestController
@RequestMapping("/comment")
public class CommentController {
    @Autowired
    private CommentService commentService;
    @Autowired
    private CommentProducer commentProducer;

    // 最简单的插入示例
    @PostMapping("/add")
    public CommentDO addComment(@RequestBody CommentDO commentDO) {
        return commentService.saveComment(commentDO);
    }

    // RabbitMQ 异步插入示例
    // MQ异步架构下，评论入库相比接口返回肯定有延迟，其他用户看到新评论也有延迟。
    // 流量小的时候应该很快就查到了，延迟不大，我就怕mysql迟迟写不进去，但是流量大的时候有延迟应该也正常，我想到最简单的是可以加数据库机器（流量大时可能数据库的压力要比mq大点，有连接瓶颈）
    // 还有，如果一个接口响应时间很长，会占用服务器资源，异步解耦的话能提高吞吐量 这个是可以测试的点
    // 前端如何显示最新评论呢？不确定 下面是copilot讲的 要极致的用户体验还是得后端机器性能够强，就不用考虑那么多了hhh
    // 方案一：前端主动轮询 我感觉这个定时的时间设长了就慢了
    // 前端定时（如每隔1秒）向后端接口（如 /comment/list）发送 GET 请求，获取最新评论列表。
    // 后端从数据库查询最新评论，返回给前端。
    // 前端刷新评论区，显示最新评论。
    // 方案二：前端评论提交后立即刷新
    // 用户提交评论后，前端等待 RabbitMQ 消费者入库（可加短暂延迟），然后请求 /comment/list 获取最新评论。
    // 适合评论量不大、实时性要求不高的场景。
    // 方案三：WebSocket 推送（高级）
    // 后端在评论入库后，通过 WebSocket 主动推送最新评论给所有在线用户。
    // 前端监听 WebSocket 消息，收到新评论后自动刷新评论区。
    // 适合高实时性场景，但需要额外实现 WebSocket 服务。
    // 方案四：让前端直接显示 这个我无法确定，万一后端失败了可能还挺麻烦？但是如果能保证不失败也不错，要控制好后端的压力
    @PostMapping("/add/rabbitmq")
    public String addCommentByRabbitMQ(@RequestBody CommentDO commentDO) {
        commentProducer.sendComment(commentDO);
        return "评论消息已发送到 RabbitMQ";
    }
}
