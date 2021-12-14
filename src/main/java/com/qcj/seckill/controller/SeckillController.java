package com.qcj.seckill.controller;/*
 *功能描述
 * @author qcj
 * @param 秒杀控制器$
 */


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.qcj.seckill.config.AccessLimit;
import com.qcj.seckill.exception.GlobalException;
import com.qcj.seckill.pojo.*;
import com.qcj.seckill.rabbitmq.MQSender;
import com.qcj.seckill.service.IGoodsService;
import com.qcj.seckill.service.IOrderService;
import com.qcj.seckill.service.ISeckillOrderService;
import com.qcj.seckill.util.JsonUtil;
import com.qcj.seckill.vo.GoodsVo;
import com.qcj.seckill.vo.RespBean;
import com.qcj.seckill.vo.RespBeanEnum;

import com.wf.captcha.ArithmeticCaptcha;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author zhoubin
 * @since 1.0.0
 */
@Controller
@Slf4j
@RequestMapping("/seckill")
public class SeckillController implements InitializingBean {

    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private ISeckillOrderService seckillOrderService;
    @Autowired
    private IOrderService orderService;

    @Autowired
    private MQSender mqSender;
    // 标记对应商品的库存是否为空，true表示空
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private DefaultRedisScript redisScript;

    private Map<Long,Boolean> emptyStockMap = new HashMap<>();

//    /**
//     * 功能描述：秒杀请求，未静态化
//     * @param model
//     * @param user
//     * @param goodsId
//     * @return
//     */
//    @RequestMapping(value = "/doSeckill2")
//    public String doSeckill(Model model, User user, Long goodsId) {
//        if (user == null) {
//            return "login";
//        }
//        model.addAttribute("user", user);
//        GoodsVo goods = goodsService.findGoodsVoByGoodsId(goodsId);
//        //判断库存
//        if (goods.getStockCount() < 1) {
//            model.addAttribute("errmsg", RespBeanEnum.EMPTY_STOCK.getMessage());
//            return "seckillFail";
//        }
//        //判断是否重复抢购
//        SeckillOrder seckillOrder = seckillOrderService.getOne(new
//                QueryWrapper<SeckillOrder>().eq("user_id", user.getId()).eq(
//                "goods_id", goodsId));
//        if (seckillOrder != null) {
//            model.addAttribute("errmsg", RespBeanEnum.REPEATE_ERROR.getMessage());
//            return "seckillFail";
//        }
//        Order order = orderService.seckill(user, goods);
//        model.addAttribute("order",order);
//        model.addAttribute("goods",goods);
//        return "orderDetail";
//    }


    /**
     * 功能描述：秒杀请求，通过静态化优化
     * @param :model
     * @param :user
     * @param :goodsId
     * @return
     */
    @PostMapping(value = "/{path}/doSeckill")
    @ResponseBody
    public RespBean doSeckill(@PathVariable String path, User user, Long goodsId) {
        if (user == null) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        // 判断请求路径是否合法，也就是是否通过点击按钮来获取，而不是直接访问页面
        boolean check = orderService.checkPath(user, goodsId,path);
        if (!check)
            // 校验秒杀，不合法
            return RespBean.error(RespBeanEnum.REQUEST_ILLEGAL);
        // 通过内存标记减少访问Redis
        if(emptyStockMap.get(goodsId))
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);


        //判断是否重复抢购
        Object seckillOrder = redisTemplate.
                opsForValue().get("order:" + user.getId() + ":" + goodsId);

        if (seckillOrder != null) {
            return RespBean.error(RespBeanEnum.REPEATE_ERROR);
        }

        // 预减库存
        ValueOperations valueOperations = redisTemplate.opsForValue();
        Long stock = valueOperations.decrement("seckillGoods:" + goodsId);
        // 用lua脚本实现原子操作
//        Long stock = (Long) redisTemplate.execute(redisScript, Collections.
//                singletonList("seckillGoods:" + goodsId), Collections.EMPTY_LIST);


        if(stock < 0){
            // 库存已空
            emptyStockMap.put(goodsId,true);
            valueOperations.increment("seckillGoods:" + goodsId);
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        SeckillMessage seckillMessage = new SeckillMessage(user, goodsId);
        String jsonString = JSON.toJSONString(seckillMessage);
        mqSender.sendseckillMessage(jsonString);
        return RespBean.success(0);


    }



    /**
         * 获取秒杀结果
         *
         * @param user
         * @param goodsId
         * @return orderId:成功，-1：秒杀失败，0：排队中
         */
    @RequestMapping(value = "/result")
    @ResponseBody
    public RespBean getResult(User user, Long goodsId) {
        if (user == null) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        Long orderId = seckillOrderService.getResult(user, goodsId);
        return RespBean.success(orderId);
    }

    /**
     * 获得真实的秒杀路径，随机生成
     * 并且会对验证码进行验证；
     * 还是先了接口限流
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping(value = "/path")
    @ResponseBody
    // 自定义的过滤注解
    @AccessLimit(second = 60,maxCount = 5,needLogin = true)
    public RespBean getPath(User user, Long goodsId,String captcha,
                            HttpServletRequest request){
        if(user == null){
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        // 已被自定义注解替代
//        ValueOperations valueOperations = redisTemplate.opsForValue();
//
//        // 接口限流,每个用户每分钟访问5次
//        String uri = request.getRequestURI();
//        Integer count = (Integer) valueOperations.get(uri + ":" + user.getId());
//        if(count == null)
//            // 如果还没有，也就是第一次登陆，那么就加入redis,设置一分钟
//            valueOperations.set(uri + ":" + user.getId(),1,1,TimeUnit.MINUTES);
//        else if(count > 5)
//            // 已经超过了5次
//            return RespBean.error(RespBeanEnum.ACCESS_LIMIT_REACHED);
//        else
//            valueOperations.increment(uri + ":" + user.getId());

        // 验证码校验
        boolean check = orderService.checkCaptcha(user,goodsId,captcha);
        if (!check)
            return RespBean.error(RespBeanEnum.CAPTCHA_ERROR);
        String str = orderService.createPath(user,goodsId);
        return RespBean.success(str);
    }

    /**
     * 生成验证码
     * @param user
     * @param goodsId
     * @param response
     */
    @GetMapping("/captcha")
    public void verifyCode(User user, Long goodsId, HttpServletResponse response) {
        if (user == null || goodsId < 0) {
            throw new GlobalException(RespBeanEnum.REQUEST_ILLEGAL);
        }
        // 设置请求头为输出图片的类型
        response.setContentType("image/jpg");
        response.setHeader("Pargam", "nocache");
        response.setHeader("Cache-control", "no-cache");
        response.setDateHeader("Expires", 0);
        //生成验证码，将结果放入redis
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(130, 32, 3);

        redisTemplate.opsForValue().set("captcha:" + user.getId() + ":" + goodsId, captcha.text(), 300, TimeUnit.SECONDS);
        try {
            captcha.out(response.getOutputStream());
        } catch (IOException e) {
            log.error("验证码生成失败", e.getMessage());

        }
    }


    /**
     启动服务器时初始化，把商品库存数量加载到Redis
     *
     * @throws :Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> list = goodsService.findGoodsVo();
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        list.forEach(goodsVo -> {
            redisTemplate.opsForValue().set("seckillGoods:" + goodsVo.getId(),
                    goodsVo.getStockCount());
            emptyStockMap.put(goodsVo.getId(), false);
        });

    }

}

