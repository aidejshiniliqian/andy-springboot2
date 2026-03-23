package com.warehouse.management.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.warehouse.management.entity.Material;
import com.warehouse.management.mapper.MaterialMapper;
import com.warehouse.management.service.MaterialService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MaterialServiceImpl extends ServiceImpl<MaterialMapper, Material> implements MaterialService {

    @Override
    public Material save(Material material) {
        this.saveOrUpdate(material);
        return material;
    }

    @Override
    public Optional<Material> findById(Long id) {
        return Optional.ofNullable(this.getById(id));
    }

    @Override
    public List<Material> findAll() {
        return this.list();
    }

    @Override
    public Page<Material> findAll(Page<Material> pageable) {
        return this.page(pageable);
    }

    @Override
    public void deleteById(Long id) {
        this.removeById(id);
    }

    @Override
    public boolean existsByCode(String code) {
        LambdaQueryWrapper<Material> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Material::getCode, code);
        return this.count(wrapper) > 0;
    }
}
