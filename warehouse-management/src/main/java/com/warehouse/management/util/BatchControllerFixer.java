package com.warehouse.management.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class BatchControllerFixer {
    public static void main(String[] args) {
        String controllerDir = "src/main/java/com/warehouse/management/controller";
        
        try (Stream<Path> paths = Files.walk(Paths.get(controllerDir))) {
            paths.filter(Files::isRegularFile)
                 .filter(path -> path.toString().endsWith("Controller.java"))
                 .forEach(BatchControllerFixer::fixController);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        System.out.println("Controller修复完成！");
    }
    
    private static void fixController(Path path) {
        try {
            // Read file
            StringBuilder content = new StringBuilder();
            try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
            }
            
            String fileContent = content.toString();
            String fileName = path.getFileName().toString();
            String entityName = fileName.replace("Controller.java", "");
            
            // Skip some files that need special handling
            if (fileName.equals("ReportController.java") || fileName.equals("AuthController.java")) {
                System.out.println("Skipping special controller: " + fileName);
                return;
            }
            
            // Replace imports
            fileContent = fileContent.replace(
                "import org.springframework.data.domain.Page;",
                "import com.baomidou.mybatisplus.extension.plugins.pagination.Page;"
            );
            fileContent = fileContent.replace("import org.springframework.data.domain.PageRequest;\n", "");
            fileContent = fileContent.replace("import org.springframework.data.domain.Pageable;\n", "");
            
            // Replace pagination object creation
            fileContent = fileContent.replace(
                "Pageable pageable = PageRequest.of(page, size);",
                "Page<" + entityName + "> pageable = new Page<>(page, size);"
            );
            
            // Replace page default value from 0 to 1
            fileContent = fileContent.replace(
                "@RequestParam(defaultValue = \"0\") int page",
                "@RequestParam(defaultValue = \"1\") int page"
            );
            
            // Write back
            try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
                writer.write(fileContent);
            }
            
            System.out.println("Fixed: " + fileName);
        } catch (IOException e) {
            System.err.println("Error fixing " + path + ": " + e.getMessage());
        }
    }
}
