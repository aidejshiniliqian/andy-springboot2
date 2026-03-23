package com.andy.warehouse.mapper;

import com.andy.warehouse.entity.Material;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Optional;

@Mapper
public interface MaterialMapper extends BaseMapper<Material> {

    @Select("SELECT * FROM material WHERE material_code = #{materialCode} AND is_deleted = false")
    Optional<Material> findByMaterialCode(@Param("materialCode") String materialCode);

    @Select("SELECT COUNT(*) > 0 FROM material WHERE material_code = #{materialCode} AND is_deleted = false")
    boolean existsByMaterialCode(@Param("materialCode") String materialCode);

    @Select("SELECT COUNT(*) > 0 FROM material WHERE barcode = #{barcode} AND is_deleted = false")
    boolean existsByBarcode(@Param("barcode") String barcode);

    @Select("<script>" +
            "SELECT * FROM material " +
            "WHERE is_deleted = false " +
            "<if test='materialCode != null'> AND material_code LIKE CONCAT('%', #{materialCode}, '%') </if> " +
            "<if test='materialName != null'> AND material_name LIKE CONCAT('%', #{materialName}, '%') </if> " +
            "<if test='categoryId != null'> AND category_id = #{categoryId} </if> " +
            "<if test='status != null'> AND status = #{status} </if> " +
            "ORDER BY created_at DESC" +
            "</script>")
    IPage<Material> findByConditions(Page<Material> page,
                                      @Param("materialCode") String materialCode,
                                      @Param("materialName") String materialName,
                                      @Param("categoryId") Long categoryId,
                                      @Param("status") Integer status);

    @Select("SELECT * FROM material WHERE category_id = #{categoryId} AND status = #{status} AND is_deleted = false")
    List<Material> findByCategoryIdAndStatus(@Param("categoryId") Long categoryId, @Param("status") Integer status);
}
