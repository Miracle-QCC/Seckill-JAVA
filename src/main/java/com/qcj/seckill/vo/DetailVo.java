package com.qcj.seckill.vo;/*
 *功能描述:详情返回对象
 * @author qcj
 * @param 详情返回对象$
 */

import com.qcj.seckill.pojo.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetailVo {
    private User user;

    private GoodsVo goodsVo;

    private int seckillStatus;

    private int remainSeconds;

}
