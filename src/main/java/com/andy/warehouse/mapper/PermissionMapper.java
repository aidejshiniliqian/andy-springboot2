package com.andy.warehouse.mapper;

import com.andy.warehouse.entity.Permission;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Optional;

@Mapper
public interface PermissionMapper extends BaseMapper<Permission> {

    @Select("SELECT * FROM sys_permission WHERE permission_code = #{permissionCode} AND is_deleted = false")
    Optional<Permission> findByPermissionCode(@Param("permissionCode") String permissionCode);

    @Select("SELECT COUNT(*) > 0 FROM sys_permission WHERE permission_code = #{permissionCode} AND is_deleted = false")
    boolean existsByPermissionCode(@Param("permissionCode") String permissionCode);

    @Select("SELECT * FROM sys_permission WHERE parent_id IS NULL AND status = #{status} AND is_deleted = false ORDER BY sort_order")
    List<Permission> findByParentIsNullAndStatusOrderBySortOrderAsc(@Param("status") Integer status);

    @Select("SELECT * FROM sys_permission WHERE parent_id = #{parentId} AND status = #{status} AND is_deleted = false ORDER BY sort_order")
    List<Permission> findByParentIdAndStatusOrderBySortOrderAsc(@Param("parentId") Long parentId, @Param("status") Integer status);

    @Select("SELECT p.* FROM sys_permission p " +
            "JOIN sys_role_permission rp ON p.id = rp.permission_id " +
            "WHERE rp.role_id = #{roleId} AND p.status = 1 AND p.is_deleted = false")
    List<Permission> findByRoleId(@Param("roleId") Long roleId);

    @Select("SELECT DISTINCT p.* FROM sys_permission p " +
            "JOIN sys_role_permission rp ON p.id = rp.permission_id " +
            "JOIN sys_user_role ur ON rp.role_id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND p.status = 1 AND p.is_deleted = false")
    List<Permission> findByUserId(@Param("userId") Long userId);
}
