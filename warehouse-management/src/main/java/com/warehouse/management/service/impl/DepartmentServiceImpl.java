package com.warehouse.management.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.warehouse.management.entity.Department;
import com.warehouse.management.mapper.DepartmentMapper;
import com.warehouse.management.service.DepartmentService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DepartmentServiceImpl extends ServiceImpl<DepartmentMapper, Department> implements DepartmentService {

    @Override
    public Department save(Department department) {
        saveOrUpdate(department);
        return department;
    }

    @Override
    public Optional<Department> findById(Long id) {
        return Optional.ofNullable(getById(id));
    }

    @Override
    public List<Department> findAll() {
        return list();
    }

    @Override
    public List<Department> findRootDepartments() {
        LambdaQueryWrapper<Department> wrapper = new LambdaQueryWrapper<>();
        wrapper.isNull(Department::getParentId);
        return list(wrapper);
    }

    @Override
    public List<Department> findByParentId(Long parentId) {
        LambdaQueryWrapper<Department> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Department::getParentId, parentId);
        return list(wrapper);
    }

    @Override
    public Page<Department> findAll(Page<Department> pageable) {
        return page(pageable);
    }

    @Override
    public void deleteById(Long id) {
        removeById(id);
    }
}
