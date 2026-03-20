package com.andy.warehouse.repository;

import com.andy.warehouse.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long>, JpaSpecificationExecutor<Department> {

    Optional<Department> findByCode(String code);

    boolean existsByCode(String code);

    @Query("SELECT d FROM Department d WHERE d.deleted = false AND d.organization.id = :orgId")
    List<Department> findByOrgId(@Param("orgId") Long orgId);

    @Query("SELECT d FROM Department d WHERE d.deleted = false AND d.parent IS NULL AND d.organization.id = :orgId")
    List<Department> findRootDepartments(@Param("orgId") Long orgId);

    @Query("SELECT d FROM Department d WHERE d.deleted = false AND d.parent.id = :parentId")
    List<Department> findByParentId(@Param("parentId") Long parentId);

    @Query("SELECT d FROM Department d WHERE d.deleted = false AND d.status = 1")
    List<Department> findAllActive();
}
