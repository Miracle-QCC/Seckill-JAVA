package com.qcj.seckill.vo;/*
 *功能描述
 * @author qcj
 * @param 公共返回对象枚举$
 */

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public enum RespBeanEnum {
    // 通用
    SUCCESS(200,"SUCCESS"), ERROR(500,"服务端异常"),
    // 登录模块 5002
    LOGIN_ERROR(500210,"用户名或账号错误"),
    MOBILE_ERRROR(500211,"手机号码不正确"),
    BIND_ERROR(500212,"参数校验异常"),
    MOBILE_NOT_EXIST(500213,"手机号码不存在"),
    PASSWORD_UPDATE_ERROR(500214,"密码更新失败"),
    SESSION_ERROR(500215,"用户不存在"),
    // 秒杀模块 5005
    EMPTY_STOCK(500500,"库存不足"),
    REPEATE_ERROR(500501,"该商品每人限购一件"),
    REQUEST_ILLEGAL(500502,"请求非法，请重新尝试"),
    CAPTCHA_ERROR(500503,"验证码错误"),
    ACCESS_LIMIT_REACHED(500504,"访问过于频繁"),

    //订单模块
    ORDER_NOT_EXIST(500300,"订单信息不存在");
    private final Integer code;
    private final String message;



}
