package com.qcj.seckill.controller;/*
 *功能描述
 * @author qcj
 * @param 登录请求$
 */


import com.qcj.seckill.service.IUserService;
import com.qcj.seckill.vo.LoginVo;
import com.qcj.seckill.vo.RespBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Controller
@RequestMapping("/login")
@Slf4j
public class LoginController {

    /*
    * 跳转登录页面
    * */
    @Autowired
    private IUserService userService;
    @RequestMapping("/toLogin")
    public String toLogin(){

        return "login";
    }

    @RequestMapping("/doLogin")
    @ResponseBody
    public RespBean doLogin(@Valid LoginVo loginVo, HttpServletRequest request, HttpServletResponse response){


        return userService.doLogin(loginVo,request,response);
    }
}
