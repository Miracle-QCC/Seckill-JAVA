package com.qcj.seckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qcj.seckill.pojo.Goods;
import com.qcj.seckill.vo.GoodsVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zhoubin
 * @since 2021-12-11
 */
@Mapper
public interface GoodsMapper extends BaseMapper<Goods> {

    /**
     * 功能描述：获取商品列表
     * @return
     */
    List<GoodsVo> findGoodsVo();

    GoodsVo findGoodsVoByGoodsId(Long goodsId);
}
