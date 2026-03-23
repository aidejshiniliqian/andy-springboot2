package com.warehouse.management.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.warehouse.management.entity.Material;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MaterialMapper extends BaseMapper<Material> {
}
