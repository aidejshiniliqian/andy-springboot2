package com.andy.warehouse.repository;

import com.andy.warehouse.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long>, JpaSpecificationExecutor<Permission> {

    Optional<Permission> findByCode(String code);

    boolean existsByCode(String code);

    @Query("SELECT p FROM Permission p WHERE p.deleted = false AND p.parentId IS NULL")
    List<Permission> findRootPermissions();

    @Query("SELECT p FROM Permission p WHERE p.deleted = false AND p.parentId = :parentId")
    List<Permission> findByParentId(@Param("parentId") Long parentId);

    @Query("SELECT p FROM Permission p WHERE p.deleted = false AND p.status = 1 ORDER BY p.sortOrder")
    List<Permission> findAllActive();

    @Query("SELECT p FROM Permission p WHERE p.deleted = false AND p.type = :type ORDER BY p.sortOrder")
    List<Permission> findByType(@Param("type") Integer type);
}
