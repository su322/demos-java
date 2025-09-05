package io.github.su322.rabbitmqdemo.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;

import java.util.HashMap;
import java.util.Map;

/**
 * 1. 消息丢失
 * 原因
 * 队列未持久化（非 durable）。
 * 消息未持久化（deliveryMode 非 2）。
 * 消费端未正确 ack，消息被 RabbitMQ 丢弃。
 * RabbitMQ 服务异常或重启，内存消息丢失。
 * 解决方案
 * 队列持久化：声明队列时设置 durable=true。
 * 消息持久化：发送消息时设置 deliveryMode=2（RabbitTemplate 默认已持久化）。
 * 生产端确认机制：开启 publisher confirm（spring.rabbitmq.publisher-confirm-type=correlated），确保消息成功投递到交换机/队列。
 * 消费端手动 ack：消费端采用手动 ack（autoAck=false），业务处理成功后再 ack，防止消息丢失。
 * 镜像队列：关键业务可用 RabbitMQ 镜像队列，提升高可用性。
 * <p>
 * 2. 消息重复
 * 原因
 * 消费端处理超时或异常，RabbitMQ 重新投递消息。
 * 消费端未正确 ack，消息被多次消费。
 * 生产端重试机制导致消息重复发送。
 * 解决方案
 * 消费端幂等性设计：消费端业务逻辑需保证幂等性（如唯一ID去重、数据库唯一约束、分布式锁等）。
 * 手动 ack：消费端处理成功后再 ack，失败时不 ack，防止误消费。
 * 消息唯一标识：每条消息带唯一ID，消费端处理前先查重。
 * 生产端去重：生产端发送消息前做去重，防止重复投递。
 * <p>
 * 3. 消息积压
 * 原因
 * 消费端处理能力低于生产端，消息堆积在队列。
 * 消费端宕机或异常，队列无人消费。
 * 消息处理逻辑慢，导致消费速率低。
 * 解决方案
 * 提升消费端并发数：增加消费者线程数（maxConcurrentConsumers），提升消费速率。
 * 优化消费逻辑：减少单条消息处理耗时，异步处理、批量处理等。
 * 限流削峰：生产端限流，防止瞬时流量过大。
 * 监控告警：监控队列长度，及时告警和扩容。
 * 临时扩容：消息积压严重时，临时增加消费端实例，快速清理积压。
 * 死信队列：设置死信队列，防止异常消息长期堆积。
 */
@Configuration
public class RabbitMQConfig {
    /**
     * RabbitTemplate Bean，生产者发送消息使用
     * 设置消息转换器为 Jackson2JsonMessageConverter，自动将对象序列化为 JSON
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(new Jackson2JsonMessageConverter());
        return template;
    }

    /**
     * 死信队列相关配置
     * 死信交换机、死信队列、绑定关系
     */
    @Bean
    public DirectExchange dlxExchange() {
        return new DirectExchange("dlx.exchange");
    }

    @Bean
    public Queue deadLetterQueue() {
        return new Queue("dead_letter_queue", true);
    }

    @Bean
    public Binding dlxBinding() {
        return BindingBuilder.bind(deadLetterQueue()).to(dlxExchange()).with("dlx.routingKey");
    }

    /**
     * 显式声明 DirectExchange、队列和绑定关系，适合后续多业务扩展
     */
    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange("direct_exchange");
    }

    @Bean
    public Queue commentQueue() {
        Map<String, Object> args = new HashMap<>();
        // 运行有error还没管
//        args.put("x-dead-letter-exchange", "dlx.exchange"); // 死信交换机
//        args.put("x-dead-letter-routing-key", "dlx.routingKey"); // 死信路由键
        return new Queue("comment_queue", true, false, false, args);
    }

    @Bean
    public Binding commentBinding() {
        return BindingBuilder.bind(commentQueue()).to(directExchange()).with("comment");
    }

    /**
     * 配置 RabbitMQ 消费端监听容器工厂
     * 设置消息转换器为 Jackson2JsonMessageConverter，自动将 JSON 反序列化为对象
     * 可根据服务器资源设置并发消费者数，提升消费能力
     */
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        factory.setConcurrentConsumers(4); // 并发消费者数，建议与CPU核心数一致
        factory.setMaxConcurrentConsumers(8); // 最大并发数，建议为CPU核心数2倍
        return factory;
    }
}
