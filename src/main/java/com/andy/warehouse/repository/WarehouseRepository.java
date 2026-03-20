package com.andy.warehouse.repository;

import com.andy.warehouse.entity.Warehouse;
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
public interface WarehouseRepository extends JpaRepository<Warehouse, Long>, JpaSpecificationExecutor<Warehouse> {

    Optional<Warehouse> findByCode(String code);

    boolean existsByCode(String code);

    @Query("SELECT w FROM Warehouse w WHERE w.deleted = false AND w.status = 1")
    List<Warehouse> findAllActive();

    @Query("SELECT w FROM Warehouse w WHERE w.deleted = false AND (:orgId IS NULL OR w.organization.id = :orgId)")
    Page<Warehouse> findByOrgId(@Param("orgId") Long orgId, Pageable pageable);

    @Query("SELECT w FROM Warehouse w WHERE w.deleted = false AND w.managerId = :managerId")
    List<Warehouse> findByManagerId(@Param("managerId") Long managerId);
}
