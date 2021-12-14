package com.qcj.seckill.util;/*
 *功能描述    Json工具类
 * @author qcj
 * @param $
 */

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;
/**
 * Json工具类
 *
 * @author zhoubin
 * @since 1.0.0
 */
public class JsonUtil {

    /**
     * 将对象转换成json字符串
     *
     * @param obj
     * @return
     */
    public static String object2JsonStr(Object obj) {
        return JSON.toJSONString(obj);
    }
    /**
     * 将字符串转换为对象
     *
     * @param <T> 泛型
     */
    public static <T> T jsonStr2Object(String jsonStr, Class<T> clazz) {
        return JSON.parseObject(jsonStr,clazz);
    }
    /**
     * 将json数据转换成pojo对象list
     * <p>Title: jsonToList</p>
     * <p>Description: </p>
     *
     * @param jsonStr
     @param beanType
      * @return
     */
//    public static <T> List<T> jsonToList(String jsonStr, Class<T> beanType) {
//        JavaType javaType =
//                objectMapper.getTypeFactory().constructParametricType(List.class, beanType);
//        try {
//            List<T> list = objectMapper.readValue(jsonStr, javaType);
//            return list;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
}

