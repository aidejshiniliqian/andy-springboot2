package com.andy.warehouse.dto.user;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserDTO {

    private Long id;
    private String username;
    private String realName;
    private String email;
    private String phone;
    private String avatar;
    private Integer gender;
    private Long orgId;
    private String orgName;
    private Long deptId;
    private String deptName;
    private Integer status;
    private LocalDateTime lastLoginTime;
    private String lastLoginIp;
    private List<Long> roleIds;
    private List<String> roleNames;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
