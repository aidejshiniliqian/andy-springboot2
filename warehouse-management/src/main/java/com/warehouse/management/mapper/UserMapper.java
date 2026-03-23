package com.warehouse.management.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.warehouse.management.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Optional;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    
    @Select("SELECT * FROM sys_user WHERE username = #{username}")
    Optional<User> findByUsername(String username);
}
