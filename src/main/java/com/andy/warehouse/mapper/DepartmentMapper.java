package com.andy.warehouse.mapper;

import com.andy.warehouse.entity.Department;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Optional;

@Mapper
public interface DepartmentMapper extends BaseMapper<Department> {

    @Select("SELECT * FROM sys_department WHERE dept_code = #{deptCode} AND is_deleted = false")
    Optional<Department> findByDeptCode(@Param("deptCode") String deptCode);

    @Select("SELECT COUNT(*) > 0 FROM sys_department WHERE dept_code = #{deptCode} AND is_deleted = false")
    boolean existsByDeptCode(@Param("deptCode") String deptCode);

    @Select("SELECT * FROM sys_department WHERE org_id = #{organizationId} AND status = #{status} AND is_deleted = false")
    List<Department> findByOrganizationIdAndStatus(@Param("organizationId") Long organizationId, @Param("status") Integer status);

    @Select("SELECT * FROM sys_department WHERE parent_id IS NULL AND org_id = #{organizationId} AND status = #{status} AND is_deleted = false ORDER BY sort_order")
    List<Department> findByParentIsNullAndOrganizationIdAndStatusOrderBySortOrderAsc(@Param("organizationId") Long organizationId, @Param("status") Integer status);

    @Select("SELECT * FROM sys_department WHERE parent_id = #{parentId} AND status = #{status} AND is_deleted = false ORDER BY sort_order")
    List<Department> findByParentIdAndStatusOrderBySortOrderAsc(@Param("parentId") Long parentId, @Param("status") Integer status);

    @Select("<script>" +
            "SELECT * FROM sys_department " +
            "WHERE is_deleted = false " +
            "<if test='orgId != null'> AND org_id = #{orgId} </if> " +
            "<if test='deptName != null'> AND dept_name LIKE CONCAT('%', #{deptName}, '%') </if> " +
            "<if test='status != null'> AND status = #{status} </if> " +
            "ORDER BY sort_order" +
            "</script>")
    List<Department> findByConditions(@Param("orgId") Long orgId,
                                      @Param("deptName") String deptName,
                                      @Param("status") Integer status);
}
