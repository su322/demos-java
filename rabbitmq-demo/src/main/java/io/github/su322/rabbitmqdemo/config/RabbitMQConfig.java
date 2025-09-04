package io.github.su322.rabbitmqdemo.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
     * 声明 comment_queue 队列，持久化，项目启动时自动创建
     */
    @Bean
    public Queue commentQueue() {
        return new Queue("comment_queue", true);
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
