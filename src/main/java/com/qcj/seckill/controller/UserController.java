package com.qcj.seckill.controller;


import com.alibaba.fastjson.JSON;
import com.qcj.seckill.pojo.SeckillMessage;
import com.qcj.seckill.pojo.User;
import com.qcj.seckill.rabbitmq.MQSender;
import com.qcj.seckill.vo.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author zhoubin
 */
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private MQSender mqSender;

    /**
     * 功能描述：用户信息（测试））
     * @param user
     * @return
     */
    @RequestMapping("/info")
    @ResponseBody
    public RespBean info(User user){
        return RespBean.success(user);
    }


    // 测试Rabbitmq发送消息
    @ResponseBody
    @RequestMapping("/mq")
    public void mq(){
        User user = new User();
        user.setId(1L);
        user.setNickname("dasdsa");
        Long goodsId = 1L;
        SeckillMessage seckillMessage = new SeckillMessage(user, goodsId);
        String jsonString = JSON.toJSONString(seckillMessage);
        mqSender.sendseckillMessage(jsonString);
    }
}
