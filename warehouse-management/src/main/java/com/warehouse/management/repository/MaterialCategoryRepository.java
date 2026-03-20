package com.warehouse.management.repository;

import com.warehouse.management.entity.MaterialCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaterialCategoryRepository extends JpaRepository<MaterialCategory, Long>, JpaSpecificationExecutor<MaterialCategory> {
    List<MaterialCategory> findByParentIdIsNull();
    List<MaterialCategory> findByParentId(Long parentId);
    boolean existsByCode(String code);
}
