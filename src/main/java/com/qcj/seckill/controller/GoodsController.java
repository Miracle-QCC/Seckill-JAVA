package com.qcj.seckill.controller;/*
 *功能描述
 * @author qcj
 * @param 商品控制$
 */

import com.qcj.seckill.pojo.User;
import com.qcj.seckill.service.IGoodsService;
import com.qcj.seckill.service.IUserService;
import com.qcj.seckill.vo.DetailVo;
import com.qcj.seckill.vo.GoodsVo;
import com.qcj.seckill.vo.RespBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Controller
@Slf4j
@RequestMapping("/goods")
public class GoodsController {
    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private IUserService userService;

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private ThymeleafViewResolver thymeleafViewResolver;

    private  int count = 0;
    /*
     * 跳转到商品列表
     * */
    @RequestMapping(value = "/toList", produces = "text/html;charset=utf-8")
    @ResponseBody
    public String toList(Model model, User user, HttpServletRequest request,
                         HttpServletResponse response) {
//        if (StringUtils.isEmpty(ticket)) {
//            return "login";
//        }
//        User user = userService.getUserByCookie(ticket, request, response);
//        if (null == user) {
//            return "login";
//        } 已经被HanlerMethodArgumentResolver代替了
        ValueOperations valueOperations = redisTemplate.opsForValue();
        //Redis中获取页面，如果不为空，直接返回页面
        String html = (String) valueOperations.get("goodsList");
        if (!StringUtils.isEmpty(html)) {
            return html;
        }
        model.addAttribute("user", user);
        model.addAttribute("goodsList", goodsService.findGoodsVo());
        // return "goodsList";
        //如果为空，手动渲染，存入Redis并返回
        WebContext context = new WebContext(request, response,
                request.getServletContext(), request.getLocale(),
                model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goodsList",
                context);
        if (!StringUtils.isEmpty(html)) {
            valueOperations.set("goodsList", html, 60, TimeUnit.SECONDS);

        }
        return html;
    }


    /**
     * 功能描述：商品详情
     */
//    @RequestMapping(value = "/toDetail2/{goodsId}", produces = "text/html;charset=utf-8")
//    @ResponseBody
//    public String toDetail(Model model, User user, @PathVariable Long goodsId,
//                           HttpServletRequest request, HttpServletResponse response) {
//        ValueOperations valueOperations = redisTemplate.opsForValue();
//        //Redis中获取页面，如果不为空，直接返回页面
//        String html = (String) valueOperations.get("goodsDetail:" + goodsId);
//        if (!StringUtils.isEmpty(html)) {
//            return html;
//        }
//        model.addAttribute("user", user);
//        GoodsVo goods = goodsService.findGoodsVoByGoodsId(goodsId);
//        model.addAttribute("goods", goods);
//        Date startDate = goods.getStartDate();
//        Date endDate = goods.getEndDate();
//        Date nowDate = new Date();
//        //秒杀状态
//        int seckillStatus = 0;
//        //剩余开始时间
//        int remainSeconds = 0;
//        //秒杀还未开始
//        if (nowDate.before(startDate)) {
//            remainSeconds = (int) ((startDate.getTime() - nowDate.getTime()) /
//                    1000);
//            // 秒杀已结束
//        } else if (nowDate.after(endDate)) {
//            seckillStatus = 2;
//            remainSeconds = -1;
//            // 秒杀中
//        } else {
//            seckillStatus = 1;
//            remainSeconds = 0;
//        }
//        model.addAttribute("seckillStatus", seckillStatus);
//        model.addAttribute("remainSeconds", remainSeconds);
//        // return "goodsDetail";
//        //如果为空，手动渲染，存入Redis并返回
//        WebContext context = new WebContext(request, response,
//                request.getServletContext(), request.getLocale(),
//
//                model.asMap());
//        html = thymeleafViewResolver.getTemplateEngine().process("goodsDetail",
//                context);
//        if (!StringUtils.isEmpty(html)) {
//            valueOperations.set("goodsDetail:" + goodsId, html, 60,
//                    TimeUnit.SECONDS);
//        }
//        return html;
//    }

    /**
     * 功能描述：商品详情,经过了页面静态化的优化
     */
    @RequestMapping(value = "/detail/{goodsId}")
    @ResponseBody
    public RespBean toDetail(HttpServletRequest request, HttpServletResponse
            response, Model model, User user,
                             @PathVariable Long goodsId) {
        GoodsVo goods = goodsService.findGoodsVoByGoodsId(goodsId);
        Date startDate = goods.getStartDate();
        Date endDate = goods.getEndDate();
        Date nowDate = new Date();

        //秒杀状态
        int secKillStatus = 0;
        //剩余开始时间
        int remainSeconds = 0;
        //秒杀还未开始
        if (nowDate.before(startDate)) {
            remainSeconds = (int) ((startDate.getTime() - nowDate.getTime()) / 1000);
            // 秒杀已结束
        } else if (nowDate.after(endDate)) {
            secKillStatus = 2;
            remainSeconds = -1;
            // 秒杀中
        } else {
            secKillStatus = 1;
            remainSeconds = 0;
        }
        DetailVo detailVo = new DetailVo();
        detailVo.setGoodsVo(goods);
        detailVo.setUser(user);
        detailVo.setRemainSeconds(remainSeconds);
        detailVo.setSeckillStatus(secKillStatus);
        return RespBean.success(detailVo);
    }
}