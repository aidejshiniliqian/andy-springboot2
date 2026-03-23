package com.warehouse.management.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class ControllerUpdater {
    public static void main(String[] args) throws IOException {
        String controllerDir = "src/main/java/com/warehouse/management/controller";
        
        try (Stream<Path> paths = Files.walk(Paths.get(controllerDir))) {
            paths.filter(Files::isRegularFile)
                 .filter(path -> path.toString().endsWith("Controller.java"))
                 .filter(path -> !path.getFileName().toString().equals("InventoryController.java"))
                 .filter(path -> !path.getFileName().toString().equals("MaterialController.java"))
                 .filter(path -> !path.getFileName().toString().equals("ReportController.java"))
                 .filter(path -> !path.getFileName().toString().equals("AuthController.java"))
                 .forEach(ControllerUpdater::updateController);
        }
        
        System.out.println("Controller更新完成！");
    }
    
    private static void updateController(Path path) {
        try {
            String content = Files.readString(path);
            
            // 获取实体类名
            String fileName = path.getFileName().toString();
            String entityName = fileName.replace("Controller.java", "");
            
            // 替换导入语句
            content = content.replace("import org.springframework.data.domain.Page;", 
                                     "import com.baomidou.mybatisplus.extension.plugins.pagination.Page;");
            content = content.replace("import org.springframework.data.domain.PageRequest;\n", "");
            content = content.replace("import org.springframework.data.domain.Pageable;\n", "");
            
            // 替换分页对象创建
            content = content.replace("Pageable pageable = PageRequest.of(page, size);", 
                                     "Page<" + entityName + "> pageable = new Page<>(page, size);");
            
            // 替换默认页码
            content = content.replace("@RequestParam(defaultValue = \"0\") int page", 
                                     "@RequestParam(defaultValue = \"1\") int page");
            
            Files.writeString(path, content);
            System.out.println("已更新: " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
