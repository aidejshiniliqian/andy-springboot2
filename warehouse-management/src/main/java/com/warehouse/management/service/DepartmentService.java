package com.warehouse.management.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.warehouse.management.entity.Department;

import java.util.List;
import java.util.Optional;

public interface DepartmentService {
    Department save(Department department);
    Optional<Department> findById(Long id);
    List<Department> findAll();
    List<Department> findRootDepartments();
    List<Department> findByParentId(Long parentId);
    Page<Department> findAll(Page<Department> pageable);
    void deleteById(Long id);
}
