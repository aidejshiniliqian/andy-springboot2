package com.warehouse.management.repository;

import com.warehouse.management.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long>, JpaSpecificationExecutor<Permission> {
    List<Permission> findByParentIdIsNull();
    List<Permission> findByParentId(Long parentId);
}
