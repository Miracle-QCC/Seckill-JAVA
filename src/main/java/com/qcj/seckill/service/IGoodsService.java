package com.qcj.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qcj.seckill.pojo.Goods;
import com.qcj.seckill.vo.GoodsVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zhoubin
 * @since 2021-12-11
 */
public interface IGoodsService extends IService<Goods> {
    /**
     * 功能描述 :获取商品列表
     * @return
     */
    List<GoodsVo> findGoodsVo();

    /**
     * 功能描述 :获取商品详情页面
     * @return
     */
    GoodsVo findGoodsVoByGoodsId(Long goodsId);
}
