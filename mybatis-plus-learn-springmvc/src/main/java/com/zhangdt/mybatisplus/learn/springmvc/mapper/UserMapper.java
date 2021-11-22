package com.zhangdt.mybatisplus.learn.springmvc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhangdt.mybatisplus.learn.springmvc.entity.User;

public interface UserMapper extends BaseMapper<User> {

    Integer selectMaxAge();

}
