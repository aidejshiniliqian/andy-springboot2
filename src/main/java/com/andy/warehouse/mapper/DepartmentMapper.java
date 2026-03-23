package com.andy.warehouse.mapper;

import com.andy.warehouse.entity.Department;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DepartmentMapper extends BaseMapper<Department> {

    @Select("SELECT * FROM sys_department WHERE code = #{code} AND deleted = 0")
    Department findByCode(@Param("code") String code);

    @Select("SELECT * FROM sys_department WHERE deleted = 0 AND org_id = #{orgId}")
    List<Department> findByOrgId(@Param("orgId") Long orgId);

    @Select("SELECT * FROM sys_department WHERE deleted = 0 AND parent_id IS NULL AND org_id = #{orgId}")
    List<Department> findRootDepartments(@Param("orgId") Long orgId);

    @Select("SELECT * FROM sys_department WHERE deleted = 0 AND parent_id = #{parentId}")
    List<Department> findByParentId(@Param("parentId") Long parentId);

    @Select("SELECT * FROM sys_department WHERE deleted = 0 AND status = 1")
    List<Department> findAllActive();

    @Select("SELECT COUNT(*) FROM sys_department WHERE code = #{code} AND deleted = 0")
    boolean existsByCode(@Param("code") String code);
}
