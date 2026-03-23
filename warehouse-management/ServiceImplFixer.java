import java.io.*;
import java.nio.charset.StandardCharsets;

public class ServiceImplFixer {
    public static void main(String[] args) throws Exception {
        String serviceImplDir = "d:/codes/github/andy-springboot2/warehouse-management/src/main/java/com/warehouse/management/service/impl";
        
        File dir = new File(serviceImplDir);
        for (File file : dir.listFiles()) {
            if (file.isFile() && file.getName().endsWith("ServiceImpl.java") && !file.getName().equals("ReportServiceImpl.java") && !file.getName().equals("UserDetailsServiceImpl.java")) {
                fixServiceImpl(file);
            }
        }
        
        System.out.println("Service实现类修复完成！");
    }
    
    private static void fixServiceImpl(File file) throws Exception {
        String fileName = file.getName();
        System.out.println("Fixing: " + fileName);
        
        // Read file
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
        }
        
        String content = sb.toString();
        
        // Get entity name
        String entityName = fileName.replace("ServiceImpl.java", "");
        
        // Replace imports
        content = content.replaceAll("import com.warehouse.management.repository." + entityName + "Repository;\r?\n", "");
        content = content.replaceAll("import org.springframework.data.domain.Page;\r?\n", "");
        content = content.replaceAll("import org.springframework.data.domain.Pageable;\r?\n", "");
        content = content.replaceAll("import org.springframework.data.domain.PageRequest;\r?\n", "");
        
        // Replace imports - add necessary ones
        content = content.replace(
            "import lombok.RequiredArgsConstructor;\nimport org.springframework.stereotype.Service;\n",
            "import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;\n" +
            "import com.baomidou.mybatisplus.extension.plugins.pagination.Page;\n" +
            "import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;\n" +
            "import com.warehouse.management.mapper." + entityName + "Mapper;\n" +
            "import com.warehouse.management.entity." + entityName + ";\n" +
            "import lombok.RequiredArgsConstructor;\n" +
            "import org.springframework.stereotype.Service;\n"
        );
        
        // Replace class declaration and repository field
        content = content.replace(
            "@RequiredArgsConstructor\n@Service\npublic class " + entityName + "ServiceImpl implements " + entityName + "Service {\n\n    private final " + entityName + "Repository " + entityName.toLowerCase() + "Repository;",
            "@RequiredArgsConstructor\n@Service\npublic class " + entityName + "ServiceImpl extends ServiceImpl<" + entityName + "Mapper, " + entityName + "> implements " + entityName + "Service {"
        );
        
        // Replace repository method calls with MyBatis-Plus methods
        content = content.replaceAll(entityName.toLowerCase() + "Repository.save", "this.save");
        content = content.replaceAll(entityName.toLowerCase() + "Repository.findById", "this.findById");
        content = content.replaceAll(entityName.toLowerCase() + "Repository.findAll\\(\\)", "this.list()");
        content = content.replaceAll("\\.orElse\\(null\\)", ".orElse(null)");
        
        // Replace delete method
        content = content.replaceAll(entityName.toLowerCase() + "Repository.deleteById", "this.removeById");
        
        // Fix existsByCode method
        content = content.replaceAll("return " + entityName.toLowerCase() + "Repository.existsByCode\\(code\\);", 
            "LambdaQueryWrapper<" + entityName + "> wrapper = new LambdaQueryWrapper<>();\n        wrapper.eq(" + entityName + "::getCode, code);\n        return this.count(wrapper) > 0;");
        
        // Fix findAll(Page) method
        content = content.replaceAll("return " + entityName.toLowerCase() + "Repository.findAll\\(pageable\\);", 
            "return this.page(pageable);");
        
        // Write back
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(file), StandardCharsets.UTF_8))) {
            bw.write(content);
        }
        
        System.out.println("Fixed: " + fileName);
    }
}
