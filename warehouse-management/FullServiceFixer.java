import java.io.*;
import java.nio.charset.StandardCharsets;

public class FullServiceFixer {
    public static void main(String[] args) throws Exception {
        String serviceImplDir = "d:/codes/github/andy-springboot2/warehouse-management/src/main/java/com/warehouse/management/service/impl";
        
        File dir = new File(serviceImplDir);
        for (File file : dir.listFiles()) {
            String fileName = file.getName();
            if (file.isFile() && fileName.endsWith("ServiceImpl.java") 
                && !fileName.equals("ReportServiceImpl.java") 
                && !fileName.equals("UserDetailsServiceImpl.java")
                && !fileName.equals("WarehouseServiceImpl.java")) {
                
                fixServiceImpl(file);
            }
        }
        
        System.out.println("Service实现类修复完成！");
    }
    
    private static void fixServiceImpl(File file) throws Exception {
        String fileName = file.getName();
        String entityName = fileName.replace("ServiceImpl.java", "");
        String lowerEntityName = entityName.substring(0, 1).toLowerCase() + entityName.substring(1);
        
        System.out.println("Fixing: " + fileName);
        
        // Create new content from template
        String content = generateServiceImplContent(entityName, lowerEntityName);
        
        // Write back
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(file), StandardCharsets.UTF_8))) {
            bw.write(content);
        }
        
        System.out.println("Fixed: " + fileName);
    }
    
    private static String generateServiceImplContent(String entityName, String lowerEntityName) {
        return "package com.warehouse.management.service.impl;\n" +
                "\n" +
                "import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;\n" +
                "import com.baomidou.mybatisplus.extension.plugins.pagination.Page;\n" +
                "import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;\n" +
                "import com.warehouse.management.entity." + entityName + ";\n" +
                "import com.warehouse.management.mapper." + entityName + "Mapper;\n" +
                "import com.warehouse.management.service." + entityName + "Service;\n" +
                "import lombok.RequiredArgsConstructor;\n" +
                "import org.springframework.stereotype.Service;\n" +
                "\n" +
                "import java.util.List;\n" +
                "import java.util.Optional;\n" +
                "\n" +
                "@Service\n" +
                "@RequiredArgsConstructor\n" +
                "public class " + entityName + "ServiceImpl extends ServiceImpl<" + entityName + "Mapper, " + entityName + "> implements " + entityName + "Service {\n" +
                "\n" +
                "    @Override\n" +
                "    public " + entityName + " save(" + entityName + " " + lowerEntityName + ") {\n" +
                "        this.saveOrUpdate(" + lowerEntityName + ");\n" +
                "        return " + lowerEntityName + ";\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public Optional<" + entityName + "> findById(Long id) {\n" +
                "        return Optional.ofNullable(this.getById(id));\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public List<" + entityName + "> findAll() {\n" +
                "        return this.list();\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public Page<" + entityName + "> findAll(Page<" + entityName + "> pageable) {\n" +
                "        return this.page(pageable);\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public void deleteById(Long id) {\n" +
                "        this.removeById(id);\n" +
                "    }\n" +
                hasExistsByCodeMethod(entityName) +
                "}\n";
    }
    
    private static String hasExistsByCodeMethod(String entityName) {
        // Only certain entities have existsByCode method
        if (entityName.equals("Warehouse") || entityName.equals("Material") || 
            entityName.equals("MaterialCategory") || entityName.equals("Role")) {
            return "\n    @Override\n" +
                   "    public boolean existsByCode(String code) {\n" +
                   "        LambdaQueryWrapper<" + entityName + "> wrapper = new LambdaQueryWrapper<>();\n" +
                   "        wrapper.eq(" + entityName + "::getCode, code);\n" +
                   "        return this.count(wrapper) > 0;\n" +
                   "    }\n";
        }
        return "";
    }
}
