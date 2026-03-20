package com.andy.warehouse.repository;

import com.andy.warehouse.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long>, JpaSpecificationExecutor<Role> {

    Optional<Role> findByCode(String code);

    boolean existsByCode(String code);

    @Query("SELECT r FROM Role r WHERE r.deleted = false AND r.status = 1")
    List<Role> findAllActive();

    @Query("SELECT r FROM Role r JOIN r.permissions p WHERE p.id = :permissionId AND r.deleted = false")
    List<Role> findByPermissionId(@Param("permissionId") Long permissionId);
}
