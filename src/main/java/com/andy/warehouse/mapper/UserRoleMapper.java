package com.andy.warehouse.mapper;

import com.andy.warehouse.entity.UserRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserRoleMapper extends BaseMapper<UserRole> {

    @Select("SELECT role_id FROM sys_user_role WHERE user_id = #{userId}")
    List<Long> findRoleIdsByUserId(@Param("userId") Long userId);

    @Select("SELECT r.* FROM sys_role r INNER JOIN sys_user_role ur ON r.id = ur.role_id WHERE ur.user_id = #{userId} AND r.deleted = 0")
    List<com.andy.warehouse.entity.Role> findRolesByUserId(@Param("userId") Long userId);

    void deleteByUserId(@Param("userId") Long userId);
}
