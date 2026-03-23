package com.warehouse.management.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class EntityFixer {
    
    private static final Map<String, String> TABLE_NAMES = new HashMap<>();
    
    static {
        TABLE_NAMES.put("User.java", "sys_user");
        TABLE_NAMES.put("Role.java", "sys_role");
        TABLE_NAMES.put("Permission.java", "sys_permission");
        TABLE_NAMES.put("Department.java", "sys_department");
        TABLE_NAMES.put("Organization.java", "sys_organization");
        TABLE_NAMES.put("Warehouse.java", "biz_warehouse");
        TABLE_NAMES.put("Material.java", "biz_material");
        TABLE_NAMES.put("MaterialCategory.java", "biz_material_category");
        TABLE_NAMES.put("InStock.java", "biz_in_stock");
        TABLE_NAMES.put("InStockDetail.java", "biz_in_stock_detail");
        TABLE_NAMES.put("OutStock.java", "biz_out_stock");
        TABLE_NAMES.put("OutStockDetail.java", "biz_out_stock_detail");
        TABLE_NAMES.put("Inventory.java", "biz_inventory");
    }
    
    public static void main(String[] args) {
        String entityDir = "src/main/java/com/warehouse/management/entity";
        
        try (Stream<Path> paths = Files.walk(Paths.get(entityDir))) {
            paths.filter(Files::isRegularFile)
                 .filter(path -> path.toString().endsWith(".java"))
                 .filter(path -> !path.getFileName().toString().equals("BaseEntity.java"))
                 .forEach(EntityFixer::fixEntity);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        System.out.println("实体类修复完成！");
    }
    
    private static void fixEntity(Path path) {
        try {
            String fileName = path.getFileName().toString();
            System.out.println("Fixing entity: " + fileName);
            
            // Read file
            StringBuilder content = new StringBuilder();
            try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
            }
            
            String fileContent = content.toString();
            
            // Replace imports
            fileContent = fileContent.replace("import jakarta.persistence.*;", 
                "import com.baomidou.mybatisplus.annotation.TableField;\n" +
                "import com.baomidou.mybatisplus.annotation.TableName;");
            
            // Get table name
            String tableName = TABLE_NAMES.getOrDefault(fileName, 
                "biz_" + fileName.replace(".java", "").toLowerCase());
            
            // Replace @Entity and @Table with @TableName
            fileContent = fileContent.replaceAll("@Entity\\s*", "");
            fileContent = fileContent.replaceAll("@Table\\(name = \".*?\"\\)", 
                "@TableName(\"" + tableName + "\")");
            
            // Replace @Column with @TableField
            fileContent = fileContent.replaceAll("@Column\\(.*?\\)", "@TableField");
            
            // Handle relationships - @ManyToOne, @OneToMany, @ManyToMany
            fileContent = handleRelationships(fileContent);
            
            // Write back
            try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
                writer.write(fileContent);
            }
            
            System.out.println("Fixed: " + fileName);
        } catch (IOException e) {
            System.err.println("Error fixing " + path + ": " + e.getMessage());
        }
    }
    
    private static String handleRelationships(String content) {
        // Pattern for @ManyToOne or @OneToOne with @JoinColumn
        Pattern joinPattern = Pattern.compile(
            "@ManyToOne.*?@JoinColumn\\(name = \"(.*?)\"\\)\\s*private\\s*(\\w+)\\s*(\\w+);",
            Pattern.DOTALL);
        
        Matcher matcher = joinPattern.matcher(content);
        StringBuffer sb = new StringBuffer();
        
        while (matcher.find()) {
            String columnName = matcher.group(1);
            String type = matcher.group(2);
            String name = matcher.group(3);
            
            // Create foreign key field
            String idField = "    @TableField(\"" + columnName + "\")\n" +
                           "    private Long " + name + "Id;\n\n";
            
            // Create non-persistent field
            String objectField = "    @TableField(exist = false)\n" +
                               "    private " + type + " " + name + ";";
            
            matcher.appendReplacement(sb, idField + objectField);
        }
        matcher.appendTail(sb);
        content = sb.toString();
        
        // Remove remaining @OneToMany and @ManyToMany
        content = content.replaceAll("@OneToMany.*?\\)\\s*private", "    @TableField(exist = false)\n    private");
        content = content.replaceAll("@ManyToMany.*?\\)\\s*private", "    @TableField(exist = false)\n    private");
        
        // Remove FetchType and other JPA related imports
        content = content.replaceAll("import jakarta.persistence.FetchType;", "");
        
        return content;
    }
}
