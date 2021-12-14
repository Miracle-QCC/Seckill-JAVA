package com.qcj.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qcj.seckill.pojo.Order;
import com.qcj.seckill.pojo.User;
import com.qcj.seckill.vo.GoodsVo;
import com.qcj.seckill.vo.OrderDetailVo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zhoubin
 * @since 2021-12-11
 */
public interface IOrderService extends IService<Order> {


    /**
     * 功能描述：秒杀
     * @param user
     * @param goods
     * @return
     */
    Order seckill(User user, GoodsVo goods);

    /**
     * 功能描述：返回订单详情
     * @param orderId
     * @return
     */
    OrderDetailVo detail(Long orderId);

    /**
     * 功能描述：获取秒杀地址
     * @param user
     * @param goodsId
     * @return
     */
    String createPath(User user, Long goodsId);

    /**
     * 对秒杀访问进行校验
     * @param user
     * @param goodsId
     * @return
     */
    boolean checkPath(User user, Long goodsId,String path);

    /**
     * 校验验证码
     * @param user
     * @param goodsId
     * @param captcha
     * @return
     */
    boolean checkCaptcha(User user, Long goodsId, String captcha);
}
