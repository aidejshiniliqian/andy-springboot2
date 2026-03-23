package com.andy.warehouse.mapper;

import com.andy.warehouse.entity.Organization;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrganizationMapper extends BaseMapper<Organization> {

    @Select("SELECT * FROM sys_organization WHERE code = #{code} AND deleted = 0")
    Organization findByCode(@Param("code") String code);

    @Select("SELECT * FROM sys_organization WHERE deleted = 0 AND parent_id IS NULL")
    List<Organization> findRootOrganizations();

    @Select("SELECT * FROM sys_organization WHERE deleted = 0 AND parent_id = #{parentId}")
    List<Organization> findByParentId(@Param("parentId") Long parentId);

    @Select("SELECT * FROM sys_organization WHERE deleted = 0 AND status = 1")
    List<Organization> findAllActive();

    @Select("SELECT COUNT(*) FROM sys_organization WHERE code = #{code} AND deleted = 0")
    boolean existsByCode(@Param("code") String code);
}
