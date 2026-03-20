package com.andy.warehouse.repository;

import com.andy.warehouse.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    Optional<Permission> findByPermissionCode(String permissionCode);

    boolean existsByPermissionCode(String permissionCode);

    List<Permission> findByParentIsNullAndStatusOrderBySortOrderAsc(Integer status);

    List<Permission> findByParentIdAndStatusOrderBySortOrderAsc(Long parentId, Integer status);

    @Query("SELECT p FROM Permission p JOIN p.roles r WHERE r.id = :roleId AND p.status = 1")
    List<Permission> findByRoleId(@Param("roleId") Long roleId);

    @Query("SELECT p FROM Permission p JOIN p.roles r JOIN r.users u WHERE u.id = :userId AND p.status = 1")
    List<Permission> findByUserId(@Param("userId") Long userId);
}
