package com.andy.warehouse.service;

import com.andy.warehouse.dto.department.DepartmentCreateRequest;
import com.andy.warehouse.dto.department.DepartmentDTO;
import com.andy.warehouse.dto.department.DepartmentUpdateRequest;

import java.util.List;

public interface DepartmentService {

    DepartmentDTO createDepartment(DepartmentCreateRequest request);

    DepartmentDTO updateDepartment(Long id, DepartmentUpdateRequest request);

    void deleteDepartment(Long id);

    DepartmentDTO getDepartmentById(Long id);

    List<DepartmentDTO> getDepartmentTree(Long orgId);

    List<DepartmentDTO> getDepartmentsByOrgId(Long orgId);

    void updateDepartmentStatus(Long id, Integer status);
}
