package com.warehouse.management.service.impl;

import com.warehouse.management.entity.Department;
import com.warehouse.management.repository.DepartmentRepository;
import com.warehouse.management.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;

    @Override
    public Department save(Department department) {
        return departmentRepository.save(department);
    }

    @Override
    public Optional<Department> findById(Long id) {
        return departmentRepository.findById(id);
    }

    @Override
    public List<Department> findAll() {
        return departmentRepository.findAll();
    }

    @Override
    public List<Department> findRootDepartments() {
        return departmentRepository.findByParentIdIsNull();
    }

    @Override
    public List<Department> findByParentId(Long parentId) {
        return departmentRepository.findByParentId(parentId);
    }

    @Override
    public Page<Department> findAll(Pageable pageable) {
        return departmentRepository.findAll(pageable);
    }

    @Override
    public void deleteById(Long id) {
        departmentRepository.deleteById(id);
    }
}
