package com.qcj.seckill.config;/*
 *功能描述  ：Topic模式的交换机
 * @author qcj
 * @param Topic模式的交换机$
 */

import com.rabbitmq.client.ConnectionFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
/**
 * @author zhoubin
 * @since 1.0.0
 */
@Configuration
public class RabbitMQTopicConfig {

    private static final String QUEUE = "seckillQueue";

    private static final String EXCHANGE = "seckillExchange";


    @Bean
    public Queue queue(){
        return new Queue(QUEUE);
    }
    @Bean
    public TopicExchange topicExchange(){
        return new TopicExchange(EXCHANGE);
    }
    @Bean
    public Binding binding01(){
        return BindingBuilder.bind(queue()).to(topicExchange()).with("seckill.#");
    }
}
