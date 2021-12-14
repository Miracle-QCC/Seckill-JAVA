package com.qcj.seckill.config;/*
 *功能描述
 * @author qcj
 * @param 自定义用户参数$
 */

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.qcj.seckill.pojo.User;
import com.qcj.seckill.service.IUserService;
import com.qcj.seckill.util.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Component
public class UserArgumentResolve implements HandlerMethodArgumentResolver {
    @Autowired
    private IUserService userService;
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        Class<?> clazz = parameter.getParameterType();
        return clazz == User.class;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        return UserContext.getUser();
//        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
//        HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
////         放到了拦截器中去做
////        String userTicket = CookieUtil.getCookieValue(request, "userTicket");
////        if(StringUtils.isBlank(userTicket)){
////            return null;
////        }
////        return userService.getUserByCookie(userTicket,request,response);

    }
}
