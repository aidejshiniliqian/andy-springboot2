package com.andy.warehouse.service.impl;

import com.andy.warehouse.common.BusinessException;
import com.andy.warehouse.dto.DepartmentCreateRequest;
import com.andy.warehouse.dto.DepartmentUpdateRequest;
import com.andy.warehouse.entity.Department;
import com.andy.warehouse.mapper.DepartmentMapper;
import com.andy.warehouse.mapper.OrganizationMapper;
import com.andy.warehouse.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentMapper departmentMapper;
    private final OrganizationMapper organizationMapper;

    @Override
    @Transactional
    public Department create(DepartmentCreateRequest request) {
        if (request.getCode() != null && departmentMapper.existsByCode(request.getCode())) {
            throw new BusinessException("部门编码已存在");
        }
        Department dept = new Department();
        dept.setName(request.getName());
        dept.setCode(request.getCode());
        dept.setDescription(request.getDescription());
        dept.setStatus(request.getStatus());
        dept.setOrgId(request.getOrgId());
        dept.setParentId(request.getParentId());
        departmentMapper.insert(dept);
        return dept;
    }

    @Override
    @Transactional
    public Department update(DepartmentUpdateRequest request) {
        Department dept = departmentMapper.selectById(request.getId());
        if (dept == null || dept.getDeleted()) {
            throw new BusinessException("部门不存在");
        }
        if (request.getName() != null) {
            dept.setName(request.getName());
        }
        if (request.getDescription() != null) {
            dept.setDescription(request.getDescription());
        }
        if (request.getStatus() != null) {
            dept.setStatus(request.getStatus());
        }
        if (request.getParentId() != null) {
            if (request.getParentId().equals(dept.getId())) {
                throw new BusinessException("不能将自己设置为父级");
            }
            dept.setParentId(request.getParentId());
        }
        departmentMapper.updateById(dept);
        return dept;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Department dept = departmentMapper.selectById(id);
        if (dept == null) {
            throw new BusinessException("部门不存在");
        }
        List<Department> children = departmentMapper.findByParentId(id);
        if (!children.isEmpty()) {
            throw new BusinessException("存在子部门，无法删除");
        }
        dept.setDeleted(true);
        departmentMapper.updateById(dept);
    }

    @Override
    public Department getById(Long id) {
        Department dept = departmentMapper.selectById(id);
        if (dept == null || dept.getDeleted()) {
            throw new BusinessException("部门不存在");
        }
        return dept;
    }

    @Override
    public List<Department> getByOrgId(Long orgId) {
        return departmentMapper.findByOrgId(orgId);
    }

    @Override
    public List<Department> getRootDepartments(Long orgId) {
        return departmentMapper.findRootDepartments(orgId);
    }

    @Override
    public List<Department> getChildren(Long parentId) {
        return departmentMapper.findByParentId(parentId);
    }

    @Override
    public List<Department> getTree(Long orgId) {
        List<Department> all = departmentMapper.findByOrgId(orgId);
        Map<Long, Department> map = new HashMap<>();
        List<Department> roots = new ArrayList<>();
        for (Department dept : all) {
            map.put(dept.getId(), dept);
            dept.setChildren(new ArrayList<>());
        }
        for (Department dept : all) {
            if (dept.getParentId() == null) {
                roots.add(dept);
            } else {
                Department parent = map.get(dept.getParentId());
                if (parent != null) {
                    parent.getChildren().add(dept);
                }
            }
        }
        return roots;
    }
}
