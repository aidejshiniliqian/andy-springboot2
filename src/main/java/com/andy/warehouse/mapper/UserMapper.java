package com.andy.warehouse.mapper;

import com.andy.warehouse.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Select("SELECT * FROM sys_user WHERE username = #{username} AND deleted = 0")
    User findByUsername(@Param("username") String username);

    @Select("SELECT * FROM sys_user WHERE deleted = 0 AND status = 1")
    List<User> findAllActive();

    @Select("SELECT * FROM sys_user WHERE deleted = 0 AND org_id = #{orgId}")
    IPage<User> findByOrgId(Page<User> page, @Param("orgId") Long orgId);

    @Select("SELECT u.* FROM sys_user u INNER JOIN sys_user_role ur ON u.id = ur.user_id WHERE ur.role_id = #{roleId} AND u.deleted = 0")
    List<User> findByRoleId(@Param("roleId") Long roleId);

    @Select("SELECT * FROM sys_user WHERE dept_id = #{deptId} AND deleted = 0")
    List<User> findByDeptId(@Param("deptId") Long deptId);

    @Select("SELECT COUNT(*) FROM sys_user WHERE username = #{username} AND deleted = 0")
    boolean existsByUsername(@Param("username") String username);
}
