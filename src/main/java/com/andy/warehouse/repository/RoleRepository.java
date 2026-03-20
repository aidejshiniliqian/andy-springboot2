package com.andy.warehouse.repository;

import com.andy.warehouse.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByRoleCode(String roleCode);

    boolean existsByRoleCode(String roleCode);

    @Query("SELECT r FROM Role r WHERE r.isDeleted = false AND " +
           "(:roleCode IS NULL OR r.roleCode LIKE %:roleCode%) AND " +
           "(:roleName IS NULL OR r.roleName LIKE %:roleName%) AND " +
           "(:status IS NULL OR r.status = :status)")
    Page<Role> findByConditions(@Param("roleCode") String roleCode,
                                @Param("roleName") String roleName,
                                @Param("status") Integer status,
                                Pageable pageable);

    List<Role> findByStatus(Integer status);
}
