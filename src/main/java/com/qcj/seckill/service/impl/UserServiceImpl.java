package com.qcj.seckill.service.impl;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qcj.seckill.exception.GlobalException;
import com.qcj.seckill.mapper.UserMapper;
import com.qcj.seckill.pojo.User;
import com.qcj.seckill.service.IUserService;
import com.qcj.seckill.util.CookieUtil;
import com.qcj.seckill.util.MD5Util;
import com.qcj.seckill.util.UUIDUtil;
import com.qcj.seckill.vo.LoginVo;
import com.qcj.seckill.vo.RespBean;
import com.qcj.seckill.vo.RespBeanEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zhoubin
 * @since 2021-12-10
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;
    // 登录
    @Override

    public RespBean doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response) {

        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();
        User user = userMapper.selectById(mobile);

        // 如果获取不到，就返回错误
        if(user == null)
            throw new GlobalException(RespBeanEnum.LOGIN_ERROR);

        // 判断密码是否正确
        if(!MD5Util.fromPassTpDBPass(password,user.getSlat()).equals(user.getPassword()))
            throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
        //生成cookie
        String ticket = UUIDUtil.uuid();
        redisTemplate.opsForValue().set("user"+ticket,user);
//        request.getSession().setAttribute(ticket,user);
        CookieUtil.setCookie(request,response,"userTicket",ticket);
        return RespBean.success(ticket);
    }

    @Override
    public User getUserByCookie(String userTicket,HttpServletRequest request,HttpServletResponse response) {
        if(StringUtils.isBlank(userTicket))
            return null;
        User user = (User) redisTemplate.opsForValue().get("user"+userTicket);
        if(user != null){
            CookieUtil.setCookie(request,response,"userTicket",userTicket);
        }
        return user;
    }



    /**
     * 功能描述：更新密码
     * @param userTicket
     * @param id
     * @param password
     * @param request
     * @param response
     * @return
     */
    @Override
    public RespBean updatePassword(String userTicket, Long id, String password,
                                   HttpServletRequest request,HttpServletResponse response) {
        User user = getUserByCookie(userTicket,request,response);
        if(user == null)
            throw new GlobalException(RespBeanEnum.MOBILE_NOT_EXIST);
        user.setPassword(MD5Util.inputPassToDBPass(password,user.getSlat()));
        int result = userMapper.updateById(user);
        if(1 == result){
            // 删除redis
            redisTemplate.delete("user"+userTicket);
            return RespBean.success();
        }
        return RespBean.error(RespBeanEnum.PASSWORD_UPDATE_ERROR);
    }
}
