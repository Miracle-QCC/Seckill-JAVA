package com.qcj.seckill.vo;/*
 *功能描述
 * @author qcj
 * @param 公共返回对象$
 */

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RespBean {
    private long code;
    private String message;
    private Object object;

    /*
    * 成功的返回结果
    * */
    public static RespBean success(){
        return  new RespBean(RespBeanEnum.SUCCESS.getCode(),RespBeanEnum.SUCCESS.getMessage(), null );
    }

    /*
     * 成功的返回结果，带对象的
     * */
    public static RespBean success(Object obj){

        return  new RespBean(RespBeanEnum.SUCCESS.getCode(),RespBean.success().getMessage(),obj);
    }

    /*
    * 功能描述：失败的返回结果;失败有很多原因，所以要传枚举类型
    * */
    public static RespBean error(RespBeanEnum respBeanEnum){
        return  new RespBean(respBeanEnum.getCode(),respBeanEnum.getMessage(), null);    }
    //  带对象的
    public static RespBean error(RespBeanEnum respBeanEnum,Object obj){
        return  new RespBean(respBeanEnum.getCode(),respBeanEnum.getMessage(), obj);
    }
}
