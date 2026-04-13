package com.andy.warehouse.service;

import com.andy.warehouse.dto.DepartmentCreateRequest;
import com.andy.warehouse.dto.DepartmentUpdateRequest;
import com.andy.warehouse.entity.Department;

import java.util.List;

public interface DepartmentService {

    Department create(DepartmentCreateRequest request);

    Department update(DepartmentUpdateRequest request);

    void delete(Long id);

    Department getById(Long id);

    List<Department> getByOrgId(Long orgId);

    List<Department> getRootDepartments(Long orgId);

    List<Department> getChildren(Long parentId);

    List<Department> getTree(Long orgId);
}
