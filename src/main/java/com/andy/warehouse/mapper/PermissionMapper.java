package com.andy.warehouse.mapper;

import com.andy.warehouse.entity.Permission;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PermissionMapper extends BaseMapper<Permission> {

    @Select("SELECT * FROM sys_permission WHERE code = #{code} AND deleted = 0")
    Permission findByCode(@Param("code") String code);

    @Select("SELECT * FROM sys_permission WHERE deleted = 0 AND parent_id IS NULL")
    List<Permission> findRootPermissions();

    @Select("SELECT * FROM sys_permission WHERE deleted = 0 AND parent_id = #{parentId}")
    List<Permission> findByParentId(@Param("parentId") Long parentId);

    @Select("SELECT * FROM sys_permission WHERE deleted = 0 AND status = 1 ORDER BY sort_order")
    List<Permission> findAllActive();

    @Select("SELECT * FROM sys_permission WHERE deleted = 0 AND type = #{type} ORDER BY sort_order")
    List<Permission> findByType(@Param("type") Integer type);

    @Select("SELECT COUNT(*) FROM sys_permission WHERE code = #{code} AND deleted = 0")
    boolean existsByCode(@Param("code") String code);
}
