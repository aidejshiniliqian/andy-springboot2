package com.andy.warehouse.repository;

import com.andy.warehouse.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.isDeleted = false AND " +
           "(:username IS NULL OR u.username LIKE %:username%) AND " +
           "(:realName IS NULL OR u.realName LIKE %:realName%) AND " +
           "(:orgId IS NULL OR u.organization.id = :orgId) AND " +
           "(:deptId IS NULL OR u.department.id = :deptId) AND " +
           "(:status IS NULL OR u.status = :status)")
    Page<User> findByConditions(@Param("username") String username,
                                @Param("realName") String realName,
                                @Param("orgId") Long orgId,
                                @Param("deptId") Long deptId,
                                @Param("status") Integer status,
                                Pageable pageable);
}
