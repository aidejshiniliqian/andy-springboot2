package com.andy.warehouse.service;

import com.andy.warehouse.dto.common.PageResult;
import com.andy.warehouse.dto.material.*;

import java.util.List;

public interface MaterialService {

    MaterialDTO createMaterial(MaterialCreateRequest request);

    MaterialDTO updateMaterial(Long id, MaterialUpdateRequest request);

    void deleteMaterial(Long id);

    MaterialDTO getMaterialById(Long id);

    MaterialDTO getMaterialByCode(String materialCode);

    PageResult<MaterialDTO> getMaterialList(MaterialQueryRequest request);

    List<MaterialDTO> getAllMaterials();

    List<MaterialDTO> getMaterialsByCategoryId(Long categoryId);

    void updateMaterialStatus(Long id, Integer status);
}
