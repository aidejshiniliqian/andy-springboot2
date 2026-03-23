package com.andy.warehouse.mapper;

import com.andy.warehouse.entity.MaterialCategory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MaterialCategoryMapper extends BaseMapper<MaterialCategory> {

    @Select("SELECT COUNT(*) > 0 FROM material_category WHERE category_code = #{categoryCode} AND is_deleted = false")
    boolean existsByCategoryCode(@Param("categoryCode") String categoryCode);

    @Select("SELECT * FROM material_category WHERE parent_id IS NULL AND status = #{status} AND is_deleted = false ORDER BY sort_order ASC")
    List<MaterialCategory> findByParentIsNullAndStatusOrderBySortOrderAsc(@Param("status") Integer status);

    @Select("SELECT * FROM material_category WHERE parent_id = #{parentId} AND status = #{status} AND is_deleted = false ORDER BY sort_order ASC")
    List<MaterialCategory> findByParentIdAndStatusOrderBySortOrderAsc(@Param("parentId") Long parentId, @Param("status") Integer status);

    @Select("SELECT * FROM material_category WHERE status = #{status} AND is_deleted = false")
    List<MaterialCategory> findByStatus(@Param("status") Integer status);
}
