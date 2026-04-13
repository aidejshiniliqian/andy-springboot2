package com.andy.warehouse.mapper;

import com.andy.warehouse.entity.Role;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RoleMapper extends BaseMapper<Role> {

    @Select("SELECT * FROM sys_role WHERE code = #{code} AND deleted = 0")
    Role findByCode(@Param("code") String code);

    @Select("SELECT * FROM sys_role WHERE deleted = 0 AND status = 1")
    List<Role> findAllActive();

    @Select("SELECT r.* FROM sys_role r INNER JOIN sys_role_permission rp ON r.id = rp.role_id WHERE rp.permission_id = #{permissionId} AND r.deleted = 0")
    List<Role> findByPermissionId(@Param("permissionId") Long permissionId);

    @Select("SELECT COUNT(*) FROM sys_role WHERE code = #{code} AND deleted = 0")
    boolean existsByCode(@Param("code") String code);
}
