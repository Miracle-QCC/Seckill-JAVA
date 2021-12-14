package com.qcj.seckill.rabbitmq;/*
 *功能描述 ： 消息消费者
 * @author qcj
 * @param 消息消费者$
 */

import com.alibaba.fastjson.JSON;
import com.qcj.seckill.pojo.SeckillMessage;
import com.qcj.seckill.pojo.SeckillOrder;
import com.qcj.seckill.pojo.User;
import com.qcj.seckill.service.IGoodsService;
import com.qcj.seckill.service.IOrderService;
import com.qcj.seckill.util.JsonUtil;
import com.qcj.seckill.vo.GoodsVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@Slf4j
public class MQReceiver {
    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private IOrderService orderService;

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
    }

}

