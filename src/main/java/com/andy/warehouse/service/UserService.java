package com.andy.warehouse.service;

import com.andy.warehouse.dto.*;
import com.andy.warehouse.entity.Permission;
import com.andy.warehouse.entity.Role;
import com.andy.warehouse.entity.User;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;
import java.util.Set;

public interface UserService {

    LoginResponse login(LoginRequest request);

    void logout();

    LoginResponse refreshToken(String token);

    User create(UserCreateRequest request);

    User update(UserUpdateRequest request);

    void delete(Long id);

    User getById(Long id);

    User getByUsername(String username);

    Page<User> getPage(Long orgId, Integer pageNum, Integer pageSize, String keyword);

    List<User> getByRoleId(Long roleId);

    List<User> getByDeptId(Long deptId);

    void changePassword(Long id, UserPasswordRequest request);

    void resetPassword(Long id);

    void assignRoles(Long id, Set<Long> roleIds);

    Set<Role> getUserRoles(Long id);

    Set<Permission> getUserPermissions(Long id);
}
