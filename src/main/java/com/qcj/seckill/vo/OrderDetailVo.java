package com.qcj.seckill.vo;/*
 *功能描述
 * @author qcj
 * @param 订单详情返回对象$
 */

import com.qcj.seckill.pojo.Order;
import com.qcj.seckill.pojo.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailVo {

    private Order order;
    private GoodsVo goodsVo;
}
