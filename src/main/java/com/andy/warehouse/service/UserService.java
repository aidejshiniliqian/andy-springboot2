package com.andy.warehouse.service;

import com.andy.warehouse.dto.common.PageResult;
import com.andy.warehouse.dto.user.*;

import java.util.List;

public interface UserService {

    UserDTO createUser(UserCreateRequest request);

    UserDTO updateUser(Long id, UserUpdateRequest request);

    void deleteUser(Long id);

    UserDTO getUserById(Long id);

    UserDTO getUserByUsername(String username);

    PageResult<UserDTO> getUserList(UserQueryRequest request);

    List<UserDTO> getAllUsers();

    void changePassword(Long userId, PasswordChangeRequest request);

    void resetPassword(Long userId, String newPassword);

    void updateUserStatus(Long id, Integer status);
}
