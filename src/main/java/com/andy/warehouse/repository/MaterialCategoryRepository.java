package com.andy.warehouse.repository;

import com.andy.warehouse.entity.MaterialCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MaterialCategoryRepository extends JpaRepository<MaterialCategory, Long> {

    Optional<MaterialCategory> findByCategoryCode(String categoryCode);

    boolean existsByCategoryCode(String categoryCode);

    List<MaterialCategory> findByParentIsNullAndStatusOrderBySortOrderAsc(Integer status);

    List<MaterialCategory> findByParentIdAndStatusOrderBySortOrderAsc(Long parentId, Integer status);

    List<MaterialCategory> findByStatus(Integer status);
}
