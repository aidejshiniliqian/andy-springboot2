package com.andy.warehouse.mapper;

import com.andy.warehouse.entity.Warehouse;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface WarehouseMapper extends BaseMapper<Warehouse> {

    @Select("SELECT * FROM wh_warehouse WHERE code = #{code} AND deleted = 0")
    Warehouse findByCode(@Param("code") String code);

    @Select("SELECT * FROM wh_warehouse WHERE deleted = 0 AND status = 1")
    List<Warehouse> findAllActive();

    @Select("SELECT * FROM wh_warehouse WHERE deleted = 0 AND (#{orgId} IS NULL OR org_id = #{orgId})")
    IPage<Warehouse> findByOrgId(Page<Warehouse> page, @Param("orgId") Long orgId);

    @Select("SELECT * FROM wh_warehouse WHERE deleted = 0 AND manager_id = #{managerId}")
    List<Warehouse> findByManagerId(@Param("managerId") Long managerId);

    @Select("SELECT COUNT(*) FROM wh_warehouse WHERE code = #{code} AND deleted = 0")
    boolean existsByCode(@Param("code") String code);
}
