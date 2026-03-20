package com.andy.warehouse.repository;

import com.andy.warehouse.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    Optional<Department> findByDeptCode(String deptCode);

    boolean existsByDeptCode(String deptCode);

    List<Department> findByOrganizationIdAndStatus(Long organizationId, Integer status);

    List<Department> findByParentIsNullAndOrganizationIdAndStatusOrderBySortOrderAsc(Long organizationId, Integer status);

    List<Department> findByParentIdAndStatusOrderBySortOrderAsc(Long parentId, Integer status);

    @Query("SELECT d FROM Department d WHERE d.isDeleted = false AND " +
           "(:orgId IS NULL OR d.organization.id = :orgId) AND " +
           "(:deptName IS NULL OR d.deptName LIKE %:deptName%) AND " +
           "(:status IS NULL OR d.status = :status)")
    List<Department> findByConditions(@Param("orgId") Long orgId,
                                      @Param("deptName") String deptName,
                                      @Param("status") Integer status);
}
