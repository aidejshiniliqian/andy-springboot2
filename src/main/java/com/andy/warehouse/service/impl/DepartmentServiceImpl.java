package com.andy.warehouse.service.impl;

import com.andy.warehouse.common.BusinessException;
import com.andy.warehouse.dto.DepartmentCreateRequest;
import com.andy.warehouse.dto.DepartmentUpdateRequest;
import com.andy.warehouse.entity.Department;
import com.andy.warehouse.entity.Organization;
import com.andy.warehouse.repository.DepartmentRepository;
import com.andy.warehouse.repository.OrganizationRepository;
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

    private final DepartmentRepository departmentRepository;
    private final OrganizationRepository organizationRepository;

    @Override
    @Transactional
    public Department create(DepartmentCreateRequest request) {
        if (request.getCode() != null && departmentRepository.existsByCode(request.getCode())) {
            throw new BusinessException("部门编码已存在");
        }
        Department dept = new Department();
        dept.setName(request.getName());
        dept.setCode(request.getCode());
        dept.setDescription(request.getDescription());
        dept.setStatus(request.getStatus());
        if (request.getOrgId() != null) {
            Organization org = organizationRepository.findById(request.getOrgId())
                    .orElseThrow(() -> new BusinessException("组织机构不存在"));
            dept.setOrganization(org);
        }
        if (request.getParentId() != null) {
            Department parent = departmentRepository.findById(request.getParentId())
                    .orElseThrow(() -> new BusinessException("父级部门不存在"));
            dept.setParent(parent);
        }
        return departmentRepository.save(dept);
    }

    @Override
    @Transactional
    public Department update(DepartmentUpdateRequest request) {
        Department dept = departmentRepository.findById(request.getId())
                .orElseThrow(() -> new BusinessException("部门不存在"));
        if (dept.getDeleted()) {
            throw new BusinessException("部门已被删除");
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
            Department parent = departmentRepository.findById(request.getParentId())
                    .orElseThrow(() -> new BusinessException("父级部门不存在"));
            dept.setParent(parent);
        }
        return departmentRepository.save(dept);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Department dept = departmentRepository.findById(id)
                .orElseThrow(() -> new BusinessException("部门不存在"));
        List<Department> children = departmentRepository.findByParentId(id);
        if (!children.isEmpty()) {
            throw new BusinessException("存在子部门，无法删除");
        }
        dept.setDeleted(true);
        departmentRepository.save(dept);
    }

    @Override
    public Department getById(Long id) {
        return departmentRepository.findById(id)
                .filter(d -> !d.getDeleted())
                .orElseThrow(() -> new BusinessException("部门不存在"));
    }

    @Override
    public List<Department> getByOrgId(Long orgId) {
        return departmentRepository.findByOrgId(orgId);
    }

    @Override
    public List<Department> getRootDepartments(Long orgId) {
        return departmentRepository.findRootDepartments(orgId);
    }

    @Override
    public List<Department> getChildren(Long parentId) {
        return departmentRepository.findByParentId(parentId);
    }

    @Override
    public List<Department> getTree(Long orgId) {
        List<Department> all = departmentRepository.findByOrgId(orgId);
        Map<Long, Department> map = new HashMap<>();
        List<Department> roots = new ArrayList<>();
        for (Department dept : all) {
            map.put(dept.getId(), dept);
            dept.setChildren(new ArrayList<>());
        }
        for (Department dept : all) {
            if (dept.getParent() == null) {
                roots.add(dept);
            } else {
                Department parent = map.get(dept.getParent().getId());
                if (parent != null) {
                    parent.getChildren().add(dept);
                }
            }
        }
        return roots;
    }
}
