package com.andy.warehouse.mapper;

import com.andy.warehouse.entity.RolePermission;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RolePermissionMapper extends BaseMapper<RolePermission> {

    @Select("SELECT permission_id FROM sys_role_permission WHERE role_id = #{roleId}")
    List<Long> findPermissionIdsByRoleId(@Param("roleId") Long roleId);

    @Select("SELECT p.* FROM sys_permission p INNER JOIN sys_role_permission rp ON p.id = rp.permission_id WHERE rp.role_id = #{roleId} AND p.deleted = 0")
    List<com.andy.warehouse.entity.Permission> findPermissionsByRoleId(@Param("roleId") Long roleId);

    void deleteByRoleId(@Param("roleId") Long roleId);
}
