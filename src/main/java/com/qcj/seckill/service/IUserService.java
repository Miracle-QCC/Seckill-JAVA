package com.qcj.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qcj.seckill.pojo.User;
import com.qcj.seckill.vo.LoginVo;
import com.qcj.seckill.vo.RespBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zhoubin
 * @since 2021-12-10
 */
public interface IUserService extends IService<User> {
    // 登录接口
    RespBean doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response);

    /*
    * 根据cookie获取用户
    * */
    User getUserByCookie(String userTicket,HttpServletRequest request,HttpServletResponse response);

    /**
     * 功能描述：更新密码
     */
    RespBean updatePassword(String userTicket, Long id, String password,HttpServletRequest request,
                            HttpServletResponse response);
}
