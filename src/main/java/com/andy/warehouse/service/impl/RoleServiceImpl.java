package com.andy.warehouse.service.impl;

import com.andy.warehouse.common.BusinessException;
import com.andy.warehouse.dto.RoleCreateRequest;
import com.andy.warehouse.dto.RoleUpdateRequest;
import com.andy.warehouse.entity.Permission;
import com.andy.warehouse.entity.Role;
import com.andy.warehouse.repository.PermissionRepository;
import com.andy.warehouse.repository.RoleRepository;
import com.andy.warehouse.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Override
    @Transactional
    public Role create(RoleCreateRequest request) {
        if (request.getCode() != null && roleRepository.existsByCode(request.getCode())) {
            throw new BusinessException("角色编码已存在");
        }
        Role role = new Role();
        role.setName(request.getName());
        role.setCode(request.getCode());
        role.setDescription(request.getDescription());
        role.setStatus(request.getStatus());
        if (request.getPermissionIds() != null && !request.getPermissionIds().isEmpty()) {
            Set<Permission> permissions = request.getPermissionIds().stream()
                    .map(id -> permissionRepository.findById(id)
                            .orElseThrow(() -> new BusinessException("权限不存在: " + id)))
                    .collect(Collectors.toSet());
            role.setPermissions(permissions);
        }
        return roleRepository.save(role);
    }

    @Override
    @Transactional
    public Role update(RoleUpdateRequest request) {
        Role role = roleRepository.findById(request.getId())
                .orElseThrow(() -> new BusinessException("角色不存在"));
        if (role.getDeleted()) {
            throw new BusinessException("角色已被删除");
        }
        if (request.getName() != null) {
            role.setName(request.getName());
        }
        if (request.getDescription() != null) {
            role.setDescription(request.getDescription());
        }
        if (request.getStatus() != null) {
            role.setStatus(request.getStatus());
        }
        if (request.getPermissionIds() != null) {
            Set<Permission> permissions = request.getPermissionIds().stream()
                    .map(id -> permissionRepository.findById(id)
                            .orElseThrow(() -> new BusinessException("权限不存在: " + id)))
                    .collect(Collectors.toSet());
            role.setPermissions(permissions);
        }
        return roleRepository.save(role);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new BusinessException("角色不存在"));
        role.setDeleted(true);
        roleRepository.save(role);
    }

    @Override
    public Role getById(Long id) {
        return roleRepository.findById(id)
                .filter(r -> !r.getDeleted())
                .orElseThrow(() -> new BusinessException("角色不存在"));
    }

    @Override
    public List<Role> getAll() {
        return roleRepository.findAllActive();
    }

    @Override
    public Page<Role> getPage(Integer pageNum, Integer pageSize, String keyword) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, sort);
        Specification<Role> spec = (root, query, cb) -> {
            var predicates = cb.conjunction();
            predicates = cb.and(predicates, cb.equal(root.get("deleted"), false));
            if (StringUtils.hasText(keyword)) {
                var namePredicate = cb.like(root.get("name"), "%" + keyword + "%");
                var codePredicate = cb.like(root.get("code"), "%" + keyword + "%");
                predicates = cb.and(predicates, cb.or(namePredicate, codePredicate));
            }
            return predicates;
        };
        return roleRepository.findAll(spec, pageable);
    }

    @Override
    @Transactional
    public void assignPermissions(Long id, Set<Long> permissionIds) {
        Role role = getById(id);
        Set<Permission> permissions = permissionIds.stream()
                .map(permId -> permissionRepository.findById(permId)
                        .orElseThrow(() -> new BusinessException("权限不存在: " + permId)))
                .collect(Collectors.toSet());
        role.setPermissions(permissions);
        roleRepository.save(role);
    }

    @Override
    public Set<Permission> getRolePermissions(Long id) {
        Role role = getById(id);
        return role.getPermissions();
    }
}
