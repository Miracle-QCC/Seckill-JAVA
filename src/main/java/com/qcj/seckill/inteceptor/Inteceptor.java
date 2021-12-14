package com.qcj.seckill.inteceptor;/*
 *功能描述
 * @author qcj
 * @param 拦截器$
 */

import org.springframework.ui.ModelMap;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.WebRequestInterceptor;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class Inteceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        Object loginUser = session.getAttribute("user");
        System.out.println(loginUser);
        if(loginUser != null){
            return  true;
        }
        request.setAttribute("msg", "请先登录");
        request.getRequestDispatcher("/login/toLogin").forward(request,response);
        return false;  // 拦截
    }

}

