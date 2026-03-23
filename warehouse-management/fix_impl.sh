#!/bin/bash

# Simple fix for basic ServiceImpl files

SERVICE_DIR="src/main/java/com/warehouse/management/service/impl"

for file in $(find $SERVICE_DIR -name "*ServiceImpl.java" -not -name "ReportServiceImpl.java" -not -name "UserDetailsServiceImpl.java" -not -name "WarehouseServiceImpl.java"); do
    filename=$(basename "$file")
    entityName=${filename/ServiceImpl.java/}
    lowerEntityName=$(echo "$entityName" | awk '{print tolower(substr($0,1,1)) substr($0,2)}')
    
    echo "Fixing $filename..."
    
    # Replace repository with mapper
    sed -i "s/com.warehouse.management.repository.${entityName}Repository/com.warehouse.management.mapper.${entityName}Mapper/g" "$file"
    
    # Replace imports
    sed -i '/import org.springframework.data.domain.Page/d' "$file"
    sed -i '/import org.springframework.data.domain.Pageable/d' "$file"
    
    # Replace repository field name
    sed -i "s/${lowerEntityName}Repository/${lowerEntityName}Mapper/g" "$file"
    
    # Replace method calls
    sed -i "s/${lowerEntityName}Mapper.save(/this.saveOrUpdate(/g" "$file"
    sed -i "s/${lowerEntityName}Mapper.findById(/Optional.ofNullable(this.getById(/g" "$file"
    sed -i "s/${lowerEntityName}Mapper.findAll()/this.list()/g" "$file"
    sed -i "s/${lowerEntityName}Mapper.deleteById(/this.removeById(/g" "$file"
done

echo "Done!"
