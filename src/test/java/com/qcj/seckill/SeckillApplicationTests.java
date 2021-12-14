package com.qcj.seckill;

import com.qcj.seckill.util.UUIDUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class SeckillApplicationTests {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private DefaultRedisScript<Boolean> redisScript;
    @Test
    void contextLoads() {
        ValueOperations valueOperations = redisTemplate.opsForValue();

        Boolean isLock = valueOperations.setIfAbsent("xcx","dasdas");
        if(isLock){
            valueOperations.set("name","xxxx");
            String name = (String) valueOperations.get("name");
            System.out.println("name = " + name);
            redisTemplate.delete("k1");
        }
        else{
            System.out.println("有线程在使用，请稍后");
        }


    }

    @Test
    void contextLoads1() {
        ValueOperations valueOperations = redisTemplate.opsForValue();


        // 给分布式锁设定一个过期时间
        Boolean isLock = valueOperations.setIfAbsent("xcx","dasdas",5000, TimeUnit.MILLISECONDS);
        if(isLock){
            valueOperations.set("name","xxxx");
            String name = (String) valueOperations.get("name");
            System.out.println("name = " + name);
            redisTemplate.delete("k1");
        }
        else{
            System.out.println("有线程在使用，请稍后");
        }
    }
    @Test
    public void testLUA(){
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String value = UUID.randomUUID().toString();
        Boolean aBoolean = valueOperations.setIfAbsent("k1", value, 5, TimeUnit.SECONDS);
        if(aBoolean){
            valueOperations.set("name","xxxsdas");
            Object name = valueOperations.get("name");
            System.out.println("name :" + name);
            System.out.println(valueOperations.get("k1"));
            Boolean result = (Boolean) redisTemplate.execute(redisScript, Collections.singletonList("k1"), value);
            System.out.println();
        }else{
            System.out.println("有线程在使用，请稍后使用");
        }


    }
}
