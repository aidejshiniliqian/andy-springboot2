package com.warehouse.management.service.impl;

import com.warehouse.management.entity.Material;
import com.warehouse.management.repository.MaterialRepository;
import com.warehouse.management.service.MaterialService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MaterialServiceImpl implements MaterialService {

    private final MaterialRepository materialRepository;

    @Override
    public Material save(Material material) {
        return materialRepository.save(material);
    }

    @Override
    public Optional<Material> findById(Long id) {
        return materialRepository.findById(id);
    }

    @Override
    public List<Material> findAll() {
        return materialRepository.findAll();
    }

    @Override
    public Page<Material> findAll(Pageable pageable) {
        return materialRepository.findAll(pageable);
    }

    @Override
    public void deleteById(Long id) {
        materialRepository.deleteById(id);
    }

    @Override
    public boolean existsByCode(String code) {
        return materialRepository.existsByCode(code);
    }
}
