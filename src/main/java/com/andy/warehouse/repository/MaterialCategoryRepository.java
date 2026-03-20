package com.andy.warehouse.repository;

import com.andy.warehouse.entity.MaterialCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MaterialCategoryRepository extends JpaRepository<MaterialCategory, Long>, JpaSpecificationExecutor<MaterialCategory> {

    Optional<MaterialCategory> findByCode(String code);

    boolean existsByCode(String code);

    @Query("SELECT c FROM MaterialCategory c WHERE c.deleted = false AND c.parent IS NULL")
    List<MaterialCategory> findRootCategories();

    @Query("SELECT c FROM MaterialCategory c WHERE c.deleted = false AND c.parent.id = :parentId")
    List<MaterialCategory> findByParentId(@Param("parentId") Long parentId);

    @Query("SELECT c FROM MaterialCategory c WHERE c.deleted = false AND c.status = 1")
    List<MaterialCategory> findAllActive();
}
