package com.andy.warehouse.mapper;

import com.andy.warehouse.entity.Material;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MaterialMapper extends BaseMapper<Material> {

    @Select("SELECT * FROM wh_material WHERE code = #{code} AND deleted = 0")
    Material findByCode(@Param("code") String code);

    @Select("SELECT * FROM wh_material WHERE barcode = #{barcode} AND deleted = 0")
    Material findByBarcode(@Param("barcode") String barcode);

    @Select("SELECT * FROM wh_material WHERE deleted = 0 AND status = 1")
    List<Material> findAllActive();

    @Select("SELECT * FROM wh_material WHERE deleted = 0 AND category_id = #{categoryId}")
    List<Material> findByCategoryId(@Param("categoryId") Long categoryId);

    @Select("SELECT * FROM wh_material WHERE deleted = 0 AND (#{categoryId} IS NULL OR category_id = #{categoryId})")
    IPage<Material> findByCategoryId(Page<Material> page, @Param("categoryId") Long categoryId);

    @Select("SELECT * FROM wh_material WHERE deleted = 0 AND (name LIKE CONCAT('%', #{keyword}, '%') OR code LIKE CONCAT('%', #{keyword}, '%'))")
    IPage<Material> search(Page<Material> page, @Param("keyword") String keyword);

    @Select("SELECT COUNT(*) FROM wh_material WHERE code = #{code} AND deleted = 0")
    boolean existsByCode(@Param("code") String code);
}
