package com.qcj.seckill.rabbitmq;/*
 *功能描述 ： 消息消费者
 * @author qcj
 * @param 消息消费者$
 */

import com.alibaba.fastjson.JSON;
import com.qcj.seckill.config.RabbitMQTopicConfig;
import com.qcj.seckill.pojo.SeckillMessage;
import com.qcj.seckill.pojo.SeckillOrder;
import com.qcj.seckill.pojo.User;
import com.qcj.seckill.service.IGoodsService;
import com.qcj.seckill.service.IOrderService;
import com.qcj.seckill.util.JsonUtil;
import com.qcj.seckill.vo.GoodsVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.CustomExchange;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.util.Date;

@Service
@Slf4j
public class MQReceiver {
    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private IOrderService orderService;

    @Autowired
    private RabbitTemplate rabbitTemplate;
    // 死信交换机
    @Autowired
    private CustomExchange delayedExchange;

    // 延时时间
    private Integer delayTime = 30*60*1000;  // 30分钟

    @RabbitListener(queues = "seckillQueue")
    public void receive(String msg) {
        log.info("接受的消息：" + msg);
        SeckillMessage message = JsonUtil.jsonStr2Object(msg, SeckillMessage.class);
        Long goodsId = message.getGoodsId();
        User user = message.getUser();
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
        log.info("user:{},goodsVo:{}",user,goodsVo);
        //判断库存
        if (goodsVo.getStockCount() < 1) {
            redisTemplate.opsForValue().increment("seckillGoods:" + goodsId);
            return;
        }
        // 判断是否重复抢购
        Object seckillOrder = redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId);
        if (seckillOrder != null) {
            redisTemplate.opsForValue().increment("seckillGoods:" + goodsId);
            return;
        }
        orderService.seckill(user, goodsVo);

        // 将订单添加到延时队列，如果超时（30分钟后）未处理，就取消订单
        log.info("延时{}分钟后发送：{}",delayTime / 60 / 1000,message);
        rabbitTemplate.convertAndSend(RabbitMQTopicConfig.DELAYED_EXCHANGE,"seckill.delay"
                ,msg,delaymessage -> {
                    // 发送消息前，设置延时时长
                    delaymessage.getMessageProperties().setDelay(delayTime);
                    return delaymessage;
                });
    }

    // 延迟队列接收延迟的消息后，将订单删除，并且把redis中的key也清除
    @RabbitListener(queues = RabbitMQTopicConfig.DELAYED_QUEUE)
    public void receiveDelay(String msg){
        SeckillMessage message = JsonUtil.jsonStr2Object(msg, SeckillMessage.class);
        Long goodsId = message.getGoodsId();
        User user = message.getUser();
        SeckillOrder order = (SeckillOrder) redisTemplate.opsForValue().
                get("order:" + user.getId() + ":" + goodsId);
        Long orderId = order.getOrderId();
        log.info("{}订单已经过期,所以删除订单",orderId);
        redisTemplate.delete("order:" + user.getId() + ":" + goodsId);
    }

    @RabbitListener(queues = RabbitMQTopicConfig.DELAYED_QUEUE)
    public void receiveDelayQueue(Message message) throws UnsupportedEncodingException {
        String msg = new String(message.getBody(),"UTF-8");
        log.info("收到延迟队列的消息:{}",msg);
    }

}

