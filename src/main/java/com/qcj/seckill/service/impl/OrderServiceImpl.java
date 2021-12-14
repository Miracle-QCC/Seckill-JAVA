package com.qcj.seckill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qcj.seckill.exception.GlobalException;
import com.qcj.seckill.mapper.OrderMapper;
import com.qcj.seckill.mapper.SeckillOrderMapper;
import com.qcj.seckill.pojo.Order;
import com.qcj.seckill.pojo.SeckillGoods;
import com.qcj.seckill.pojo.SeckillOrder;
import com.qcj.seckill.pojo.User;
import com.qcj.seckill.service.IGoodsService;
import com.qcj.seckill.service.IOrderService;
import com.qcj.seckill.service.ISeckillGoodsService;
import com.qcj.seckill.service.ISeckillOrderService;
import com.qcj.seckill.util.JsonUtil;
import com.qcj.seckill.util.MD5Util;
import com.qcj.seckill.util.UUIDUtil;
import com.qcj.seckill.vo.GoodsVo;
import com.qcj.seckill.vo.OrderDetailVo;
import com.qcj.seckill.vo.RespBean;
import com.qcj.seckill.vo.RespBeanEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author zhoubin
 * @since 1.0.0
 */
@Service
@Slf4j
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements
        IOrderService {
    @Autowired
    private ISeckillGoodsService seckillGoodsService;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private ISeckillOrderService seckillOrderService;

    @Autowired
    private IGoodsService goodsService;


    @Autowired
    private RedisTemplate redisTemplate;
    /* 秒杀
    * @param user
    * @param goods
    * @return
    **/
    @Override
    @Transactional
    public Order seckill(User user, GoodsVo goods) {
        //秒杀商品表减库存
        SeckillGoods seckillGoods = seckillGoodsService.getOne(new
                QueryWrapper<SeckillGoods>().eq("goods_id",
                goods.getId()));
        seckillGoods.setStockCount(seckillGoods.getStockCount() - 1);
        boolean result = seckillGoodsService.update(new
                UpdateWrapper<SeckillGoods>().setSql("stock_count = stock_count - 1").
                eq("goods_id", goods.getId()).
                gt("stock_count",0));
        // seckillGoodsService.updateById(seckillGoods);
        if (seckillGoods.getStockCount() < 1){
            // 判断是否还有库存
            redisTemplate.opsForValue().set("isStockEmpty:" + goods.getId(),"0");
            return null;
        }

        //生成订单
        Order order = new Order();
        order.setUserId(user.getId());
        order.setGoodsId(goods.getId());
        order.setDeliveryAddId(0L);
        order.setGoodsName(goods.getGoodsName());
        order.setGoodsCount(1);
        order.setGoodsPrice(seckillGoods.getSeckillPrice());
        order.setOrderChannel(1);
        order.setStatus(0);
        order.setCreateDate(new Date());
        orderMapper.insert(order);
        //生成秒杀订单
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setOrderId(order.getId());
        seckillOrder.setUserId(user.getId());
        seckillOrder.setGoodsId(goods.getId());
        seckillOrderService.save(seckillOrder);
        redisTemplate.opsForValue().set("order:" + user.getId() + ":" +
                goods.getId(), JsonUtil.object2JsonStr(seckillOrder));
        return order;

    }


    /**
     * 功能描述：订单详情
     * @param orderId
     * @return
     */
    @Override
    public OrderDetailVo detail(Long orderId) {
        if(orderId == null){
            throw new GlobalException(RespBeanEnum.ORDER_NOT_EXIST);
        }
        Order order = orderMapper.selectById(orderId);
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(order.getGoodsId());
        OrderDetailVo detailVo = new OrderDetailVo();
        detailVo.setOrder(order);
        detailVo.setGoodsVo(goodsVo);
        return detailVo;
    }

    /**
     * 功能描述：获取秒杀地址
     * @param user
     * @param goodsId
     * @return
     */
    @Override
    public String createPath(User user, Long goodsId) {
        String str = MD5Util.md5(UUIDUtil.uuid() + "123456");
        redisTemplate.opsForValue().set("seckillPath:"+user.getId()+":"+goodsId,
                str,60, TimeUnit.SECONDS);
        return str;
    }


    /**
     * 对秒杀访问进行校验
     * @param user
     * @param goodsId
     * @return
     */
    @Override
    public boolean checkPath(User user, Long goodsId,String path) {
        if(user==null || goodsId<0|| StringUtils.isEmpty(path))
            return false;
        String obj = (String) redisTemplate.opsForValue().get("seckillPath:" + user.getId() + ":" + goodsId);
        return path.equals(obj);

    }

    /**
     * 校验验证码
     * @param user
     * @param goodsId
     * @param captcha
     * @return
     */
    @Override
    public boolean checkCaptcha(User user, Long goodsId, String captcha) {
        if(user==null || goodsId < 0 || StringUtils.isEmpty(captcha)){
            return false;
        }

        String rediscaptcha = (String) redisTemplate.opsForValue().get("captcha:" + user.getId() + ":" + goodsId);
        return captcha.equals(rediscaptcha);
    }
}
