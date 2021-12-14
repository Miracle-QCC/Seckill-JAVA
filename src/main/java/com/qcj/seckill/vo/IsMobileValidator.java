package com.qcj.seckill.vo;/*
 *功能描述
 * @author qcj
 * @param 手机号码校验规则$
 */

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.qcj.seckill.util.ValidatorUtil;
import com.qcj.seckill.validator.IsMobile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class IsMobileValidator implements ConstraintValidator<IsMobile,String> {
    private boolean required = true;
    @Override
    public void initialize(IsMobile constraintAnnotation) {
        required = constraintAnnotation.required();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if(required){
            return ValidatorUtil.isMobile(value);
        }
        else{
            if(StringUtils.isBlank(value)){
                return true;
            }else {
                return ValidatorUtil.isMobile(value);
            }
        }

    }
}
