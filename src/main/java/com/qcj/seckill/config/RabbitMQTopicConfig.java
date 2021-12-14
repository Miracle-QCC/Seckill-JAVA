package com.qcj.seckill.config;/*
 *功能描述  ：Topic模式的交换机
 * @author qcj
 * @param Topic模式的交换机$
 */

import com.rabbitmq.client.ConnectionFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhoubin
 * @since 1.0.0
 */
@Configuration
public class RabbitMQTopicConfig {

    private static final String QUEUE = "seckillQueue";

    private static final String EXCHANGE = "seckillExchange";

    //死信队列和交换机
    public static final String DELAYED_QUEUE = "seckillDelayedQueue";
    public static final String DELAYED_EXCHANGE = "seckillDelayedExchange";

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
        return BindingBuilder.bind(queue()).to(topicExchange()).with("seckill.delay");
    }

    @Bean
    public Queue delayedQueue(){
        return new Queue(DELAYED_QUEUE);
    }

    @Bean
    public CustomExchange delayedExchange() {
        Map<String, Object> args = new HashMap<>();
        // 自定义交换机的类型
        args.put("x-delayed-type", "direct");
        return new CustomExchange(DELAYED_EXCHANGE, "x-delayed-message", true, false,
                args);
    }
    @Bean
    public Binding bindingDelayedQueue(@Qualifier("delayedQueue") Queue delayedQueue,
                                       @Qualifier("delayedExchange") CustomExchange
                                               delayedExchange) {
        return BindingBuilder.bind(delayedQueue).to(delayedExchange).with("seckill.delay").noargs();
    }
}
