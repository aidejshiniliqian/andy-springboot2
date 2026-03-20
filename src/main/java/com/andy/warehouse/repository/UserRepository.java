package com.andy.warehouse.repository;

import com.andy.warehouse.entity.User;
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
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    Optional<User> findByUsername(String username);

    Optional<User> findByUsernameAndDeletedFalse(String username);

    boolean existsByUsername(String username);

    @Query("SELECT u FROM User u WHERE u.deleted = false AND u.status = 1")
    List<User> findAllActive();

    @Query("SELECT u FROM User u WHERE u.deleted = false AND (:orgId IS NULL OR u.organization.id = :orgId)")
    Page<User> findByOrgId(@Param("orgId") Long orgId, Pageable pageable);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.id = :roleId AND u.deleted = false")
    List<User> findByRoleId(@Param("roleId") Long roleId);

    @Query("SELECT u FROM User u WHERE u.department.id = :deptId AND u.deleted = false")
    List<User> findByDeptId(@Param("deptId") Long deptId);
}
