package com.andy.warehouse.repository;

import com.andy.warehouse.entity.Material;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Long>, JpaSpecificationExecutor<Material> {

    Optional<Material> findByCode(String code);

    Optional<Material> findByBarcode(String barcode);

    boolean existsByCode(String code);

    @Query("SELECT m FROM Material m WHERE m.deleted = false AND m.status = 1")
    List<Material> findAllActive();

    @Query("SELECT m FROM Material m WHERE m.deleted = false AND m.category.id = :categoryId")
    List<Material> findByCategoryId(@Param("categoryId") Long categoryId);

    @Query("SELECT m FROM Material m WHERE m.deleted = false AND (:categoryId IS NULL OR m.category.id = :categoryId)")
    Page<Material> findByCategoryId(@Param("categoryId") Long categoryId, Pageable pageable);

    @Query("SELECT m FROM Material m WHERE m.deleted = false AND (m.name LIKE %:keyword% OR m.code LIKE %:keyword%)")
    Page<Material> search(@Param("keyword") String keyword, Pageable pageable);
}
