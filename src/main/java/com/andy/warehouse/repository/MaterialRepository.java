package com.andy.warehouse.repository;

import com.andy.warehouse.entity.Material;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Long> {

    Optional<Material> findByMaterialCode(String materialCode);

    boolean existsByMaterialCode(String materialCode);

    boolean existsByBarcode(String barcode);

    @Query("SELECT m FROM Material m WHERE m.isDeleted = false AND " +
           "(:materialCode IS NULL OR m.materialCode LIKE %:materialCode%) AND " +
           "(:materialName IS NULL OR m.materialName LIKE %:materialName%) AND " +
           "(:categoryId IS NULL OR m.category.id = :categoryId) AND " +
           "(:status IS NULL OR m.status = :status)")
    Page<Material> findByConditions(@Param("materialCode") String materialCode,
                                    @Param("materialName") String materialName,
                                    @Param("categoryId") Long categoryId,
                                    @Param("status") Integer status,
                                    Pageable pageable);

    List<Material> findByCategoryIdAndStatus(Long categoryId, Integer status);

    List<Material> findByStatus(Integer status);
}
