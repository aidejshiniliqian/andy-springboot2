package com.warehouse.management.service;

import com.warehouse.management.entity.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface DepartmentService {
    Department save(Department department);
    Optional<Department> findById(Long id);
    List<Department> findAll();
    List<Department> findRootDepartments();
    List<Department> findByParentId(Long parentId);
    Page<Department> findAll(Pageable pageable);
    void deleteById(Long id);
}
