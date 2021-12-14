package com.qcj.seckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qcj.seckill.pojo.User;
import org.apache.ibatis.annotations.Mapper;

import javax.annotation.ManagedBean;


/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zhoubin
 * @since 2021-12-10
 */
@Mapper
public interface UserMapper extends BaseMapper<User>{

}
