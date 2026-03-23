package com.warehouse.management.config;

import com.warehouse.management.entity.Permission;
import com.warehouse.management.entity.Role;
import com.warehouse.management.entity.User;
import com.warehouse.management.service.PermissionService;
import com.warehouse.management.service.RoleService;
import com.warehouse.management.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserService userService;
    private final RoleService roleService;
    private final PermissionService permissionService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userService.existsByUsername("admin")) {
            return;
        }

        Permission userManage = createPermission("用户管理", "USER_MANAGE", "/api/users/**", 1, 1);
        Permission roleManage = createPermission("角色管理", "ROLE_MANAGE", "/api/roles/**", 1, 2);
        Permission permissionManage = createPermission("权限管理", "PERMISSION_MANAGE", "/api/permissions/**", 1, 3);
        Permission deptManage = createPermission("部门管理", "DEPT_MANAGE", "/api/departments/**", 1, 4);
        Permission orgManage = createPermission("机构管理", "ORG_MANAGE", "/api/organizations/**", 1, 5);
        Permission warehouseManage = createPermission("仓库管理", "WAREHOUSE_MANAGE", "/api/warehouses/**", 1, 6);
        Permission materialManage = createPermission("物资管理", "MATERIAL_MANAGE", "/api/materials/**", 1, 7);
        Permission categoryManage = createPermission("分类管理", "CATEGORY_MANAGE", "/api/material-categories/**", 1, 8);
        Permission inStockManage = createPermission("入库管理", "INSTOCK_MANAGE", "/api/in-stock/**", 1, 9);
        Permission outStockManage = createPermission("出库管理", "OUTSTOCK_MANAGE", "/api/out-stock/**", 1, 10);
        Permission inventoryQuery = createPermission("库存查询", "INVENTORY_QUERY", "/api/inventory/**", 1, 11);

        Set<Permission> permissions = new HashSet<>();
        permissions.add(userManage);
        permissions.add(roleManage);
        permissions.add(permissionManage);
        permissions.add(deptManage);
        permissions.add(orgManage);
        permissions.add(warehouseManage);
        permissions.add(materialManage);
        permissions.add(categoryManage);
        permissions.add(inStockManage);
        permissions.add(outStockManage);
        permissions.add(inventoryQuery);

        Role adminRole = new Role();
        adminRole.setName("超级管理员");
        adminRole.setCode("ADMIN");
        adminRole.setDescription("系统超级管理员角色");
        adminRole.setStatus(1);
        adminRole.setPermissions(permissions);
        roleService.save(adminRole);

        Set<Role> roles = new HashSet<>();
        roles.add(adminRole);

        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRealName("管理员");
        admin.setPhone("13800138000");
        admin.setEmail("admin@warehouse.com");
        admin.setStatus(1);
        admin.setRoles(roles);
        userService.save(admin);

        System.out.println("================================================");
        System.out.println("数据初始化完成!");
        System.out.println("默认管理员账号: admin");
        System.out.println("默认管理员密码: admin123");
        System.out.println("================================================");
    }

    private Permission createPermission(String name, String code, String url, Integer type, Integer sort) {
        Permission permission = new Permission();
        permission.setName(name);
        permission.setCode(code);
        permission.setUrl(url);
        permission.setType(type);
        permission.setSort(sort);
        permission.setStatus(1);
        return permissionService.save(permission);
    }
}
