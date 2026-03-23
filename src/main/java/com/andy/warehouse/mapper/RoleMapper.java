package com.andy.warehouse.mapper;

import com.andy.warehouse.entity.Role;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Optional;

@Mapper
public interface RoleMapper extends BaseMapper<Role> {

    @Select("SELECT * FROM sys_role WHERE role_code = #{roleCode} AND is_deleted = false")
    Optional<Role> findByRoleCode(@Param("roleCode") String roleCode);

    @Select("SELECT COUNT(*) > 0 FROM sys_role WHERE role_code = #{roleCode} AND is_deleted = false")
    boolean existsByRoleCode(@Param("roleCode") String roleCode);

    @Select("<script>" +
            "SELECT * FROM sys_role " +
            "WHERE is_deleted = false " +
            "<if test='roleCode != null'> AND role_code LIKE CONCAT('%', #{roleCode}, '%') </if> " +
            "<if test='roleName != null'> AND role_name LIKE CONCAT('%', #{roleName}, '%') </if> " +
            "<if test='status != null'> AND status = #{status} </if> " +
            "ORDER BY created_at DESC" +
            "</script>")
    IPage<Role> findByConditions(Page<Role> page,
                                  @Param("roleCode") String roleCode,
                                  @Param("roleName") String roleName,
                                  @Param("status") Integer status);

    @Select("SELECT r.* FROM sys_role r " +
            "JOIN sys_user_role ur ON r.id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND r.is_deleted = false")
    List<Role> findByUserId(@Param("userId") Long userId);

    @Select("SELECT p.id FROM sys_permission p " +
            "JOIN sys_role_permission rp ON p.id = rp.permission_id " +
            "WHERE rp.role_id = #{roleId} AND p.is_deleted = false")
    List<Long> findPermissionIdsByRoleId(@Param("roleId") Long roleId);

    @Insert("INSERT INTO sys_role_permission (role_id, permission_id) VALUES (#{roleId}, #{permissionId})")
    void insertRolePermission(@Param("roleId") Long roleId, @Param("permissionId") Long permissionId);

    @Delete("DELETE FROM sys_role_permission WHERE role_id = #{roleId}")
    void deleteRolePermissionsByRoleId(@Param("roleId") Long roleId);
}
