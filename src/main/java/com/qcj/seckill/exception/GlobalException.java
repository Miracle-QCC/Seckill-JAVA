package com.qcj.seckill.exception;/*
 *功能描述
 * @author qcj
 * @param 全局异常$
 */

import com.qcj.seckill.vo.RespBeanEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GlobalException extends RuntimeException{
    private RespBeanEnum respBeanEnum;

}
