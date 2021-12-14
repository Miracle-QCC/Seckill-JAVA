package com.qcj.seckill.config;/*
 *功能描述
 * @author qcj
 * @param $
 */

import com.qcj.seckill.pojo.User;

public class UserContext {
    private static ThreadLocal<User> userHolder = new ThreadLocal<>();

    public static void setUserHolder(User user){
        userHolder.set(user);
    }

    public static User getUser(){
        return userHolder.get();
    }
}
