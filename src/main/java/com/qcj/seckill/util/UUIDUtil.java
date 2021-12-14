package com.qcj.seckill.util;/*
 *功能描述
 * @author qcj
 * @param UUID生成$
 */

import java.util.UUID;

public class UUIDUtil {
    public static String uuid(){
        return UUID.randomUUID().toString().replace("-","");
    }
}
