package com.andy.warehouse.repository;

import com.andy.warehouse.entity.Organization;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Long> {

    Optional<Organization> findByOrgCode(String orgCode);

    boolean existsByOrgCode(String orgCode);

    @Query("SELECT o FROM Organization o WHERE o.isDeleted = false AND " +
           "(:orgCode IS NULL OR o.orgCode LIKE %:orgCode%) AND " +
           "(:orgName IS NULL OR o.orgName LIKE %:orgName%) AND " +
           "(:status IS NULL OR o.status = :status)")
    Page<Organization> findByConditions(@Param("orgCode") String orgCode,
                                        @Param("orgName") String orgName,
                                        @Param("status") Integer status,
                                        Pageable pageable);

    List<Organization> findByStatus(Integer status);
}
