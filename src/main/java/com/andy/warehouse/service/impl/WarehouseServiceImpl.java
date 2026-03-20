package com.andy.warehouse.service.impl;

import com.andy.warehouse.common.BusinessException;
import com.andy.warehouse.dto.WarehouseCreateRequest;
import com.andy.warehouse.dto.WarehouseUpdateRequest;
import com.andy.warehouse.entity.Organization;
import com.andy.warehouse.entity.Warehouse;
import com.andy.warehouse.repository.OrganizationRepository;
import com.andy.warehouse.repository.WarehouseRepository;
import com.andy.warehouse.service.WarehouseService;
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

@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final OrganizationRepository organizationRepository;

    @Override
    @Transactional
    public Warehouse create(WarehouseCreateRequest request) {
        if (request.getCode() != null && warehouseRepository.existsByCode(request.getCode())) {
            throw new BusinessException("仓库编码已存在");
        }
        Warehouse warehouse = new Warehouse();
        warehouse.setName(request.getName());
        warehouse.setCode(request.getCode());
        warehouse.setAddress(request.getAddress());
        warehouse.setArea(request.getArea());
        warehouse.setCapacity(request.getCapacity());
        warehouse.setManagerId(request.getManagerId());
        warehouse.setManagerName(request.getManagerName());
        warehouse.setPhone(request.getPhone());
        warehouse.setDescription(request.getDescription());
        warehouse.setStatus(request.getStatus());
        if (request.getOrgId() != null) {
            Organization org = organizationRepository.findById(request.getOrgId())
                    .orElseThrow(() -> new BusinessException("组织机构不存在"));
            warehouse.setOrganization(org);
        }
        return warehouseRepository.save(warehouse);
    }

    @Override
    @Transactional
    public Warehouse update(WarehouseUpdateRequest request) {
        Warehouse warehouse = warehouseRepository.findById(request.getId())
                .orElseThrow(() -> new BusinessException("仓库不存在"));
        if (warehouse.getDeleted()) {
            throw new BusinessException("仓库已被删除");
        }
        if (request.getName() != null) {
            warehouse.setName(request.getName());
        }
        if (request.getAddress() != null) {
            warehouse.setAddress(request.getAddress());
        }
        if (request.getArea() != null) {
            warehouse.setArea(request.getArea());
        }
        if (request.getCapacity() != null) {
            warehouse.setCapacity(request.getCapacity());
        }
        if (request.getManagerId() != null) {
            warehouse.setManagerId(request.getManagerId());
        }
        if (request.getManagerName() != null) {
            warehouse.setManagerName(request.getManagerName());
        }
        if (request.getPhone() != null) {
            warehouse.setPhone(request.getPhone());
        }
        if (request.getDescription() != null) {
            warehouse.setDescription(request.getDescription());
        }
        if (request.getStatus() != null) {
            warehouse.setStatus(request.getStatus());
        }
        return warehouseRepository.save(warehouse);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new BusinessException("仓库不存在"));
        warehouse.setDeleted(true);
        warehouseRepository.save(warehouse);
    }

    @Override
    public Warehouse getById(Long id) {
        return warehouseRepository.findById(id)
                .filter(w -> !w.getDeleted())
                .orElseThrow(() -> new BusinessException("仓库不存在"));
    }

    @Override
    public List<Warehouse> getAll() {
        return warehouseRepository.findAllActive();
    }

    @Override
    public Page<Warehouse> getPage(Long orgId, Integer pageNum, Integer pageSize, String keyword) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, sort);
        Specification<Warehouse> spec = (root, query, cb) -> {
            var predicates = cb.conjunction();
            predicates = cb.and(predicates, cb.equal(root.get("deleted"), false));
            if (orgId != null) {
                predicates = cb.and(predicates, cb.equal(root.get("organization").get("id"), orgId));
            }
            if (StringUtils.hasText(keyword)) {
                var namePredicate = cb.like(root.get("name"), "%" + keyword + "%");
                var codePredicate = cb.like(root.get("code"), "%" + keyword + "%");
                predicates = cb.and(predicates, cb.or(namePredicate, codePredicate));
            }
            return predicates;
        };
        return warehouseRepository.findAll(spec, pageable);
    }
}
