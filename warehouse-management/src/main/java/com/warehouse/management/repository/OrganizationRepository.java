package com.warehouse.management.repository;

import com.warehouse.management.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Long>, JpaSpecificationExecutor<Organization> {
    List<Organization> findByParentIdIsNull();
    List<Organization> findByParentId(Long parentId);
}
