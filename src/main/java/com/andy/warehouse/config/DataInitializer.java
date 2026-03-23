package com.andy.warehouse.config;

import com.andy.warehouse.entity.*;
import com.andy.warehouse.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final OrganizationRepository organizationRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() == 0) {
            log.info("Initializing default data...");
            initPermissions();
            initRoles();
            initOrganizations();
            initDepartments();
            initAdminUser();
            log.info("Default data initialization completed.");
        }
    }

    private void initPermissions() {
        List<Permission> permissions = new ArrayList<>();

        permissions.add(createPermission("user:create", "创建用户", "用户管理", "BUTTON", null, null));
        permissions.add(createPermission("user:update", "更新用户", "用户管理", "BUTTON", null, null));
        permissions.add(createPermission("user:delete", "删除用户", "用户管理", "BUTTON", null, null));
        permissions.add(createPermission("user:read", "查看用户", "用户管理", "BUTTON", null, null));

        permissions.add(createPermission("role:create", "创建角色", "角色管理", "BUTTON", null, null));
        permissions.add(createPermission("role:update", "更新角色", "角色管理", "BUTTON", null, null));
        permissions.add(createPermission("role:delete", "删除角色", "角色管理", "BUTTON", null, null));
        permissions.add(createPermission("role:read", "查看角色", "角色管理", "BUTTON", null, null));

        permissions.add(createPermission("org:create", "创建机构", "机构管理", "BUTTON", null, null));
        permissions.add(createPermission("org:update", "更新机构", "机构管理", "BUTTON", null, null));
        permissions.add(createPermission("org:delete", "删除机构", "机构管理", "BUTTON", null, null));
        permissions.add(createPermission("org:read", "查看机构", "机构管理", "BUTTON", null, null));

        permissions.add(createPermission("dept:create", "创建部门", "部门管理", "BUTTON", null, null));
        permissions.add(createPermission("dept:update", "更新部门", "部门管理", "BUTTON", null, null));
        permissions.add(createPermission("dept:delete", "删除部门", "部门管理", "BUTTON", null, null));
        permissions.add(createPermission("dept:read", "查看部门", "部门管理", "BUTTON", null, null));

        permissions.add(createPermission("permission:create", "创建权限", "权限管理", "BUTTON", null, null));
        permissions.add(createPermission("permission:update", "更新权限", "权限管理", "BUTTON", null, null));
        permissions.add(createPermission("permission:delete", "删除权限", "权限管理", "BUTTON", null, null));
        permissions.add(createPermission("permission:read", "查看权限", "权限管理", "BUTTON", null, null));

        permissions.add(createPermission("warehouse:create", "创建仓库", "仓库管理", "BUTTON", null, null));
        permissions.add(createPermission("warehouse:update", "更新仓库", "仓库管理", "BUTTON", null, null));
        permissions.add(createPermission("warehouse:delete", "删除仓库", "仓库管理", "BUTTON", null, null));
        permissions.add(createPermission("warehouse:read", "查看仓库", "仓库管理", "BUTTON", null, null));

        permissions.add(createPermission("material:create", "创建物资", "物资管理", "BUTTON", null, null));
        permissions.add(createPermission("material:update", "更新物资", "物资管理", "BUTTON", null, null));
        permissions.add(createPermission("material:delete", "删除物资", "物资管理", "BUTTON", null, null));
        permissions.add(createPermission("material:read", "查看物资", "物资管理", "BUTTON", null, null));

        permissions.add(createPermission("stock:in:create", "创建入库单", "入库管理", "BUTTON", null, null));
        permissions.add(createPermission("stock:in:confirm", "确认入库", "入库管理", "BUTTON", null, null));
        permissions.add(createPermission("stock:in:cancel", "取消入库", "入库管理", "BUTTON", null, null));
        permissions.add(createPermission("stock:in:read", "查看入库单", "入库管理", "BUTTON", null, null));

        permissions.add(createPermission("stock:out:create", "创建出库单", "出库管理", "BUTTON", null, null));
        permissions.add(createPermission("stock:out:approve", "审批出库", "出库管理", "BUTTON", null, null));
        permissions.add(createPermission("stock:out:confirm", "确认出库", "出库管理", "BUTTON", null, null));
        permissions.add(createPermission("stock:out:cancel", "取消出库", "出库管理", "BUTTON", null, null));
        permissions.add(createPermission("stock:out:read", "查看出库单", "出库管理", "BUTTON", null, null));

        permissions.add(createPermission("inventory:read", "查看库存", "库存管理", "BUTTON", null, null));

        permissionRepository.saveAll(permissions);
        log.info("Created {} permissions", permissions.size());
    }

    private Permission createPermission(String code, String name, String type, String resourceType, String url, String method) {
        Permission permission = new Permission();
        permission.setPermissionCode(code);
        permission.setPermissionName(name);
        permission.setDescription(name);
        permission.setType(resourceType);
        permission.setResourceUrl(url);
        permission.setHttpMethod(method);
        permission.setStatus(1);
        return permission;
    }

    private void initRoles() {
        List<Permission> allPermissions = permissionRepository.findAll();

        Role adminRole = new Role();
        adminRole.setRoleCode("ADMIN");
        adminRole.setRoleName("系统管理员");
        adminRole.setDescription("拥有所有权限");
        adminRole.setStatus(1);
        adminRole.setPermissions(allPermissions);
        roleRepository.save(adminRole);

        Role warehouseRole = new Role();
        warehouseRole.setRoleCode("WAREHOUSE_MANAGER");
        warehouseRole.setRoleName("仓库管理员");
        warehouseRole.setDescription("仓库管理权限");
        warehouseRole.setStatus(1);
        roleRepository.save(warehouseRole);

        Role userRole = new Role();
        userRole.setRoleCode("USER");
        userRole.setRoleName("普通用户");
        userRole.setDescription("普通用户权限");
        userRole.setStatus(1);
        roleRepository.save(userRole);

        log.info("Created 3 roles");
    }

    private void initOrganizations() {
        Organization org = new Organization();
        org.setOrgCode("ORG001");
        org.setOrgName("总公司");
        org.setDescription("系统默认组织机构");
        org.setAddress("北京市");
        org.setContactPerson("管理员");
        org.setContactPhone("13800138000");
        org.setStatus(1);
        organizationRepository.save(org);

        log.info("Created default organization");
    }

    private void initDepartments() {
        Organization org = organizationRepository.findByOrgCode("ORG001").orElse(null);
        if (org == null) return;

        Department dept = new Department();
        dept.setDeptCode("DEPT001");
        dept.setDeptName("管理部");
        dept.setDescription("系统管理部门");
        dept.setOrganization(org);
        dept.setSortOrder(0);
        dept.setStatus(1);
        departmentRepository.save(dept);

        log.info("Created default department");
    }

    private void initAdminUser() {
        Role adminRole = roleRepository.findByRoleCode("ADMIN").orElse(null);
        Organization org = organizationRepository.findByOrgCode("ORG001").orElse(null);
        Department dept = departmentRepository.findByDeptCode("DEPT001").orElse(null);

        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRealName("系统管理员");
        admin.setEmail("admin@warehouse.com");
        admin.setPhone("13800138000");
        admin.setGender(1);
        admin.setOrganization(org);
        admin.setDepartment(dept);
        admin.setStatus(1);

        if (adminRole != null) {
            admin.setRoles(Arrays.asList(adminRole));
        }

        userRepository.save(admin);
        log.info("Created admin user: admin/admin123");
    }
}
