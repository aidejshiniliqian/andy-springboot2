package com.andy.warehouse.service.impl;

import com.andy.warehouse.dto.department.DepartmentCreateRequest;
import com.andy.warehouse.dto.department.DepartmentDTO;
import com.andy.warehouse.dto.department.DepartmentUpdateRequest;
import com.andy.warehouse.entity.Department;
import com.andy.warehouse.entity.Organization;
import com.andy.warehouse.exception.BusinessException;
import com.andy.warehouse.exception.ResourceNotFoundException;
import com.andy.warehouse.mapper.DepartmentMapper;
import com.andy.warehouse.mapper.OrganizationMapper;
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

    private final DepartmentMapper departmentMapper;
    private final OrganizationMapper organizationMapper;

    @Override
    @Transactional
    public DepartmentDTO createDepartment(DepartmentCreateRequest request) {
        Organization org = organizationMapper.selectById(request.getOrgId());
        if (org == null) {
            throw new ResourceNotFoundException("组织机构不存在");
        }

        if (departmentMapper.existsByDeptCode(request.getDeptCode())) {
            throw new BusinessException("部门编码已存在");
        }

        Department dept = new Department();
        BeanUtils.copyProperties(request, dept);
        dept.setStatus(1);

        departmentMapper.insert(dept);
        return convertToDTO(dept);
    }

    @Override
    @Transactional
    public DepartmentDTO updateDepartment(Long id, DepartmentUpdateRequest request) {
        Department dept = departmentMapper.selectById(id);
        if (dept == null) {
            throw new ResourceNotFoundException("部门不存在");
        }

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
            dept.setParentId(request.getParentId());
        }

        departmentMapper.updateById(dept);
        return convertToDTO(dept);
    }

    @Override
    @Transactional
    public void deleteDepartment(Long id) {
        Department dept = departmentMapper.selectById(id);
        if (dept == null) {
            throw new ResourceNotFoundException("部门不存在");
        }
        departmentMapper.deleteById(id);
    }

    @Override
    public DepartmentDTO getDepartmentById(Long id) {
        Department dept = departmentMapper.selectById(id);
        if (dept == null) {
            throw new ResourceNotFoundException("部门不存在");
        }
        return convertToDTO(dept);
    }

    @Override
    public List<DepartmentDTO> getDepartmentTree(Long orgId) {
        List<Department> rootDepts = departmentMapper.findByParentIsNullAndOrganizationIdAndStatusOrderBySortOrderAsc(orgId, 1);
        return rootDepts.stream()
                .map(this::convertToTreeDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<DepartmentDTO> getDepartmentsByOrgId(Long orgId) {
        return departmentMapper.findByOrganizationIdAndStatus(orgId, 1).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateDepartmentStatus(Long id, Integer status) {
        Department dept = departmentMapper.selectById(id);
        if (dept == null) {
            throw new ResourceNotFoundException("部门不存在");
        }
        dept.setStatus(status);
        departmentMapper.updateById(dept);
    }

    private DepartmentDTO convertToDTO(Department dept) {
        DepartmentDTO dto = new DepartmentDTO();
        BeanUtils.copyProperties(dept, dto);
        if (dept.getParentId() != null) {
            dto.setParentId(dept.getParentId());
            Department parent = departmentMapper.selectById(dept.getParentId());
            if (parent != null) {
                dto.setParentName(parent.getDeptName());
            }
        }
        if (dept.getOrgId() != null) {
            dto.setOrgId(dept.getOrgId());
            Organization org = organizationMapper.selectById(dept.getOrgId());
            if (org != null) {
                dto.setOrgName(org.getOrgName());
            }
        }
        return dto;
    }

    private DepartmentDTO convertToTreeDTO(Department dept) {
        DepartmentDTO dto = convertToDTO(dept);
        List<Department> children = departmentMapper.findByParentIdAndStatusOrderBySortOrderAsc(dept.getId(), 1);
        if (!CollectionUtils.isEmpty(children)) {
            List<DepartmentDTO> childrenDTO = children.stream()
                    .filter(child -> !Boolean.TRUE.equals(child.getIsDeleted()) && child.getStatus() == 1)
                    .map(this::convertToTreeDTO)
                    .collect(Collectors.toList());
            dto.setChildren(childrenDTO);
        }
        return dto;
    }
}
