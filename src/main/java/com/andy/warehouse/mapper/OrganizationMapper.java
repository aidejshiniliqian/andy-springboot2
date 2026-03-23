package com.andy.warehouse.mapper;

import com.andy.warehouse.entity.Organization;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Optional;

@Mapper
public interface OrganizationMapper extends BaseMapper<Organization> {

    @Select("SELECT * FROM sys_organization WHERE org_code = #{orgCode} AND is_deleted = false")
    Optional<Organization> findByOrgCode(@Param("orgCode") String orgCode);

    @Select("SELECT COUNT(*) > 0 FROM sys_organization WHERE org_code = #{orgCode} AND is_deleted = false")
    boolean existsByOrgCode(@Param("orgCode") String orgCode);

    @Select("<script>" +
            "SELECT * FROM sys_organization " +
            "WHERE is_deleted = false " +
            "<if test='orgCode != null'> AND org_code LIKE CONCAT('%', #{orgCode}, '%') </if> " +
            "<if test='orgName != null'> AND org_name LIKE CONCAT('%', #{orgName}, '%') </if> " +
            "<if test='status != null'> AND status = #{status} </if> " +
            "ORDER BY created_at DESC" +
            "</script>")
    IPage<Organization> findByConditions(Page<Organization> page,
                                          @Param("orgCode") String orgCode,
                                          @Param("orgName") String orgName,
                                          @Param("status") Integer status);

    @Select("SELECT * FROM sys_organization WHERE status = #{status} AND is_deleted = false")
    List<Organization> findByStatus(@Param("status") Integer status);
}
