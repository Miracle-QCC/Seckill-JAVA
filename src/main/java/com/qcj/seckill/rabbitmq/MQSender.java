package com.qcj.seckill.rabbitmq;/*
 *功能描述    消息发送者
 * @author qcj
 * @param 消息发送者$
 */

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import com.qcj.seckill.pojo.SeckillMessage;
import com.qcj.seckill.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class MQSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendseckillMessage(String message) {
        log.info("发送消息：" + message);
        rabbitTemplate.convertAndSend("seckillExchange", "seckill.msg", message);
    }

//    public void send(String hello) {
//
//        log.info("发送消息：" + hello);
//        rabbitTemplate.convertAndSend("seckillExchange", "seckill.msg", hello);
//
//    }
//    public void sendseckillMessage(Object msg){
//        log.info("发送消息:{}",msg);
//        rabbitTemplate.convertAndSend("queue",msg);
//    }
}

