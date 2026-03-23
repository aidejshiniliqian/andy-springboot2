package com.andy.warehouse.mapper;

import com.andy.warehouse.entity.MaterialCategory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MaterialCategoryMapper extends BaseMapper<MaterialCategory> {

    @Select("SELECT * FROM wh_material_category WHERE code = #{code} AND deleted = 0")
    MaterialCategory findByCode(@Param("code") String code);

    @Select("SELECT * FROM wh_material_category WHERE deleted = 0 AND parent_id IS NULL")
    List<MaterialCategory> findRootCategories();

    @Select("SELECT * FROM wh_material_category WHERE deleted = 0 AND parent_id = #{parentId}")
    List<MaterialCategory> findByParentId(@Param("parentId") Long parentId);

    @Select("SELECT * FROM wh_material_category WHERE deleted = 0 AND status = 1")
    List<MaterialCategory> findAllActive();

    @Select("SELECT COUNT(*) FROM wh_material_category WHERE code = #{code} AND deleted = 0")
    boolean existsByCode(@Param("code") String code);
}
