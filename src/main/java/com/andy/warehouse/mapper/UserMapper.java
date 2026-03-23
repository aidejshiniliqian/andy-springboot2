package com.andy.warehouse.mapper;

import com.andy.warehouse.entity.User;
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
public interface UserMapper extends BaseMapper<User> {

    @Select("SELECT * FROM sys_user WHERE username = #{username} AND is_deleted = false")
    Optional<User> findByUsername(@Param("username") String username);

    @Select("SELECT COUNT(*) > 0 FROM sys_user WHERE username = #{username} AND is_deleted = false")
    boolean existsByUsername(@Param("username") String username);

    @Select("SELECT COUNT(*) > 0 FROM sys_user WHERE email = #{email} AND is_deleted = false")
    boolean existsByEmail(@Param("email") String email);

    @Select("<script>" +
            "SELECT u.* FROM sys_user u " +
            "WHERE u.is_deleted = false " +
            "<if test='username != null'> AND u.username LIKE CONCAT('%', #{username}, '%') </if> " +
            "<if test='realName != null'> AND u.real_name LIKE CONCAT('%', #{realName}, '%') </if> " +
            "<if test='orgId != null'> AND u.org_id = #{orgId} </if> " +
            "<if test='deptId != null'> AND u.dept_id = #{deptId} </if> " +
            "<if test='status != null'> AND u.status = #{status} </if> " +
            "ORDER BY u.created_at DESC" +
            "</script>")
    IPage<User> findByConditions(Page<User> page,
                                  @Param("username") String username,
                                  @Param("realName") String realName,
                                  @Param("orgId") Long orgId,
                                  @Param("deptId") Long deptId,
                                  @Param("status") Integer status);

    @Select("SELECT r.id FROM sys_role r " +
            "JOIN sys_user_role ur ON r.id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND r.is_deleted = false")
    List<Long> findRoleIdsByUserId(@Param("userId") Long userId);

    @Insert("INSERT INTO sys_user_role (user_id, role_id) VALUES (#{userId}, #{roleId})")
    void insertUserRole(@Param("userId") Long userId, @Param("roleId") Long roleId);

    @Delete("DELETE FROM sys_user_role WHERE user_id = #{userId}")
    void deleteUserRolesByUserId(@Param("userId") Long userId);
}
