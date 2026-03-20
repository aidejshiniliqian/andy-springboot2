package com.andy.warehouse.service.impl;

import com.andy.warehouse.dto.department.DepartmentCreateRequest;
import com.andy.warehouse.dto.department.DepartmentDTO;
import com.andy.warehouse.dto.department.DepartmentUpdateRequest;
import com.andy.warehouse.entity.Department;
import com.andy.warehouse.entity.Organization;
import com.andy.warehouse.exception.BusinessException;
import com.andy.warehouse.exception.ResourceNotFoundException;
import com.andy.warehouse.repository.DepartmentRepository;
import com.andy.warehouse.repository.OrganizationRepository;
import com.andy.warehouse.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final OrganizationRepository organizationRepository;

    @Override
    @Transactional
    public DepartmentDTO createDepartment(DepartmentCreateRequest request) {
        Organization org = organizationRepository.findById(request.getOrgId())
                .orElseThrow(() -> new ResourceNotFoundException("组织机构不存在"));

        if (departmentRepository.existsByDeptCode(request.getDeptCode())) {
            throw new BusinessException("部门编码已存在");
        }

        Department dept = new Department();
        BeanUtils.copyProperties(request, dept);
        dept.setOrganization(org);
        dept.setStatus(1);

        if (request.getParentId() != null) {
            Department parent = departmentRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("父部门不存在"));
            dept.setParent(parent);
        }

        Department savedDept = departmentRepository.save(dept);
        return convertToDTO(savedDept);
    }

    @Override
    @Transactional
    public DepartmentDTO updateDepartment(Long id, DepartmentUpdateRequest request) {
        Department dept = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("部门不存在"));

        if (StringUtils.hasText(request.getDeptName())) {
            dept.setDeptName(request.getDeptName());
        }
        if (StringUtils.hasText(request.getDescription())) {
            dept.setDescription(request.getDescription());
        }
        if (request.getSortOrder() != null) {
            dept.setSortOrder(request.getSortOrder());
        }
        if (request.getStatus() != null) {
            dept.setStatus(request.getStatus());
        }

        if (request.getParentId() != null) {
            Department parent = departmentRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("父部门不存在"));
            dept.setParent(parent);
        }

        Department updatedDept = departmentRepository.save(dept);
        return convertToDTO(updatedDept);
    }

    @Override
    @Transactional
    public void deleteDepartment(Long id) {
        Department dept = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("部门不存在"));
        dept.setIsDeleted(true);
        departmentRepository.save(dept);
    }

    @Override
    public DepartmentDTO getDepartmentById(Long id) {
        Department dept = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("部门不存在"));
        return convertToDTO(dept);
    }

    @Override
    public List<DepartmentDTO> getDepartmentTree(Long orgId) {
        List<Department> rootDepts = departmentRepository.findByParentIsNullAndOrganizationIdAndStatusOrderBySortOrderAsc(orgId, 1);
        return rootDepts.stream()
                .map(this::convertToTreeDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<DepartmentDTO> getDepartmentsByOrgId(Long orgId) {
        return departmentRepository.findByOrganizationIdAndStatus(orgId, 1).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateDepartmentStatus(Long id, Integer status) {
        Department dept = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("部门不存在"));
        dept.setStatus(status);
        departmentRepository.save(dept);
    }

    private DepartmentDTO convertToDTO(Department dept) {
        DepartmentDTO dto = new DepartmentDTO();
        BeanUtils.copyProperties(dept, dto);
        if (dept.getParent() != null) {
            dto.setParentId(dept.getParent().getId());
            dto.setParentName(dept.getParent().getDeptName());
        }
        if (dept.getOrganization() != null) {
            dto.setOrgId(dept.getOrganization().getId());
            dto.setOrgName(dept.getOrganization().getOrgName());
        }
        return dto;
    }

    private DepartmentDTO convertToTreeDTO(Department dept) {
        DepartmentDTO dto = convertToDTO(dept);
        if (!CollectionUtils.isEmpty(dept.getChildren())) {
            List<DepartmentDTO> children = dept.getChildren().stream()
                    .filter(child -> !Boolean.TRUE.equals(child.getIsDeleted()) && child.getStatus() == 1)
                    .map(this::convertToTreeDTO)
                    .collect(Collectors.toList());
            dto.setChildren(children);
        }
        return dto;
    }
}
