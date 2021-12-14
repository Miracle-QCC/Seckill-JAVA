package com.qcj.seckill.vo;/*
 *功能描述
 * @author qcj
 * @param 登录参数：账号和密码$
 */

import com.qcj.seckill.validator.IsMobile;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class LoginVo {
    @IsMobile
    private String mobile;

    @NotNull
    @Length(min = 6,max = 32)
    private String password;
}
