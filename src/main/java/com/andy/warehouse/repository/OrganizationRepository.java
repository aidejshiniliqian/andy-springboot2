package com.andy.warehouse.repository;

import com.andy.warehouse.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Long>, JpaSpecificationExecutor<Organization> {

    Optional<Organization> findByCode(String code);

    boolean existsByCode(String code);

    @Query("SELECT o FROM Organization o WHERE o.deleted = false AND o.parent IS NULL")
    List<Organization> findRootOrganizations();

    @Query("SELECT o FROM Organization o WHERE o.deleted = false AND o.parent.id = :parentId")
    List<Organization> findByParentId(@Param("parentId") Long parentId);

    @Query("SELECT o FROM Organization o WHERE o.deleted = false AND o.status = 1")
    List<Organization> findAllActive();
}
