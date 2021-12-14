package com.qcj.seckill.config;/*
 *功能描述   拦截器实现接口限流
 * @author qcj
 * @param 拦截器实现接口限流$
 */

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qcj.seckill.pojo.User;
import com.qcj.seckill.service.IUserService;
import com.qcj.seckill.util.CookieUtil;
import com.qcj.seckill.vo.RespBean;
import com.qcj.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

@Component
public class AccessLimitInterceptor implements HandlerInterceptor {

    @Autowired
    private IUserService userService;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(handler instanceof HandlerMethod){
            User user = getUser(request,response);
            UserContext.setUserHolder(user);
            HandlerMethod hm = (HandlerMethod) handler;
            AccessLimit accessLimit =  hm.getMethodAnnotation(AccessLimit.class);
            if(accessLimit == null)
                return true;
            int seconde = accessLimit.second();
            int maxCount = accessLimit.maxCount();
            boolean needLogin = accessLimit.needLogin();
            String key = request.getRequestURI();
            if(needLogin){
               if(user == null){
                    render(response, RespBeanEnum.SESSION_ERROR);
                    return false;
               }
               key+=":" + user.getId();
            }
            ValueOperations valueOperations = redisTemplate.opsForValue();
            Integer count = (Integer) valueOperations.get(key);
            if(count == null)
                valueOperations.set(key,1,seconde, TimeUnit.SECONDS);
            else if(count > maxCount){
                render(response,RespBeanEnum.ACCESS_LIMIT_REACHED);
                return false;
            }


            else
                valueOperations.increment(key);
        }

        return true;
    }

    private void render(HttpServletResponse response, RespBeanEnum respBeanEnum) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter writer = response.getWriter();
        RespBean respBean = RespBean.error(respBeanEnum);
        writer.write(new ObjectMapper().writeValueAsString(respBean));
        writer.flush();
        writer.close();

    }

    /**
     * 获取当前请求用户
     * @param request
     * @param response
     * @return
     */
    private User getUser(HttpServletRequest request, HttpServletResponse response) {
        String userTicket = CookieUtil.getCookieValue(request, "userTicket");
        if(StringUtils.isBlank(userTicket)){
            return null;
        }
        return userService.getUserByCookie(userTicket,request,response);
    }

}
