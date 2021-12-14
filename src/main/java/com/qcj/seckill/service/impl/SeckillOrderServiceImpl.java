package com.qcj.seckill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qcj.seckill.mapper.SeckillOrderMapper;
import com.qcj.seckill.pojo.SeckillOrder;
import com.qcj.seckill.pojo.User;
import com.qcj.seckill.service.ISeckillOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zhoubin
 * @since 2021-12-11
 */
@Service
public class SeckillOrderServiceImpl extends ServiceImpl<SeckillOrderMapper, SeckillOrder> implements ISeckillOrderService {
    @Autowired
    private SeckillOrderMapper seckillOrderMapper;

    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 获取秒杀结果
     *
     * @param user
     * @param goodsId
     * @return
     */
    @Override
    public Long getResult(User user, Long goodsId) {
        SeckillOrder seckillOrder = seckillOrderMapper.selectOne(new
                QueryWrapper<SeckillOrder>().eq("user_id", user.getId()).
                eq("goods_id", goodsId));
        if (null != seckillOrder) {
            return seckillOrder.getId();
        } else if (redisTemplate.hasKey("isStockEmpty:" + goodsId)) {
                return -1L;
        }else {
                return 0L;
            }
    }

}

