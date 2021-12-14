package com.qcj.seckill.config;/*
 *功能描述
 * @author qcj
 * @param 通用接口限流$
 */

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AccessLimit {
    int second();

    int maxCount();

    boolean needLogin() default  true;
}

