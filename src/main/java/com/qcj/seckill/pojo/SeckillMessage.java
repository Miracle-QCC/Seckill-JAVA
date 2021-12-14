package com.qcj.seckill.pojo;


/*
 *功能描述  秒杀信息
 * @author qcj
 * @param 秒杀信息$
 */

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeckillMessage implements Serializable {
    private User user;
    private Long goodsId;
}
