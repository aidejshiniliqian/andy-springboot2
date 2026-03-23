-- =====================================================
-- 仓库物资管理系统数据库初始化脚本
-- =====================================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS warehouse_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE warehouse_db;

-- =====================================================
-- 系统管理模块表结构
-- =====================================================

-- 用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    real_name VARCHAR(50) COMMENT '真实姓名',
    phone VARCHAR(11) COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    status INT DEFAULT 1 COMMENT '状态 1:启用 0:禁用',
    department_id BIGINT COMMENT '部门ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT '用户表';

-- 角色表
CREATE TABLE IF NOT EXISTS sys_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL COMMENT '角色名称',
    code VARCHAR(50) UNIQUE COMMENT '角色编码',
    description VARCHAR(200) COMMENT '角色描述',
    status INT DEFAULT 1 COMMENT '状态 1:启用 0:禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT '角色表';

-- 权限表
CREATE TABLE IF NOT EXISTS sys_permission (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL COMMENT '权限名称',
    code VARCHAR(100) COMMENT '权限编码',
    url VARCHAR(200) COMMENT '请求URL',
    method VARCHAR(50) COMMENT '请求方法',
    type INT COMMENT '权限类型 1:菜单 2:按钮',
    sort INT DEFAULT 0 COMMENT '排序',
    status INT DEFAULT 1 COMMENT '状态 1:启用 0:禁用',
    parent_id BIGINT COMMENT '父级ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT '权限表';

-- 部门表
CREATE TABLE IF NOT EXISTS sys_department (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL COMMENT '部门名称',
    description VARCHAR(200) COMMENT '部门描述',
    sort INT DEFAULT 0 COMMENT '排序',
    status INT DEFAULT 1 COMMENT '状态 1:启用 0:禁用',
    parent_id BIGINT COMMENT '父级部门ID',
    organization_id BIGINT COMMENT '组织机构ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT '部门表';

-- 组织机构表
CREATE TABLE IF NOT EXISTS sys_organization (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL COMMENT '机构名称',
    description VARCHAR(200) COMMENT '机构描述',
    address VARCHAR(200) COMMENT '机构地址',
    contact VARCHAR(20) COMMENT '联系人',
    phone VARCHAR(11) COMMENT '联系电话',
    sort INT DEFAULT 0 COMMENT '排序',
    status INT DEFAULT 1 COMMENT '状态 1:启用 0:禁用',
    parent_id BIGINT COMMENT '父级机构ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT '组织机构表';

-- 用户角色关联表
CREATE TABLE IF NOT EXISTS sys_user_role (
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    PRIMARY KEY (user_id, role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT '用户角色关联表';

-- 角色权限关联表
CREATE TABLE IF NOT EXISTS sys_role_permission (
    role_id BIGINT NOT NULL COMMENT '角色ID',
    permission_id BIGINT NOT NULL COMMENT '权限ID',
    PRIMARY KEY (role_id, permission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT '角色权限关联表';

-- =====================================================
-- 仓库管理模块表结构
-- =====================================================

-- 仓库表
CREATE TABLE IF NOT EXISTS biz_warehouse (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL COMMENT '仓库名称',
    code VARCHAR(50) UNIQUE COMMENT '仓库编码',
    address VARCHAR(200) COMMENT '仓库地址',
    manager VARCHAR(50) COMMENT '仓库管理员',
    phone VARCHAR(11) COMMENT '联系电话',
    remark VARCHAR(500) COMMENT '备注',
    status INT DEFAULT 1 COMMENT '状态 1:启用 0:禁用',
    sort INT DEFAULT 0 COMMENT '排序',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT '仓库表';

-- 物资分类表
CREATE TABLE IF NOT EXISTS biz_material_category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL COMMENT '分类名称',
    code VARCHAR(50) UNIQUE COMMENT '分类编码',
    remark VARCHAR(500) COMMENT '备注',
    status INT DEFAULT 1 COMMENT '状态 1:启用 0:禁用',
    sort INT DEFAULT 0 COMMENT '排序',
    parent_id BIGINT COMMENT '父级分类ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT '物资分类表';

-- 物资表
CREATE TABLE IF NOT EXISTS biz_material (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL COMMENT '物资名称',
    code VARCHAR(50) UNIQUE COMMENT '物资编码',
    spec VARCHAR(50) COMMENT '规格型号',
    unit VARCHAR(50) COMMENT '计量单位',
    price DECIMAL(10,2) COMMENT '单价',
    remark VARCHAR(500) COMMENT '备注',
    status INT DEFAULT 1 COMMENT '状态 1:启用 0:禁用',
    category_id BIGINT COMMENT '分类ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT '物资表';

-- =====================================================
-- 出入库管理模块表结构
-- =====================================================

-- 入库单表
CREATE TABLE IF NOT EXISTS biz_in_stock (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_no VARCHAR(50) NOT NULL UNIQUE COMMENT '入库单号',
    supplier VARCHAR(100) COMMENT '供应商',
    supplier_phone VARCHAR(20) COMMENT '供应商电话',
    supplier_address VARCHAR(200) COMMENT '供应商地址',
    type INT COMMENT '入库类型 1:采购入库 2:退货入库 3:调拨入库',
    in_stock_time DATETIME COMMENT '入库时间',
    remark VARCHAR(500) COMMENT '备注',
    status INT DEFAULT 1 COMMENT '状态 1:已完成 0:草稿',
    warehouse_id BIGINT COMMENT '仓库ID',
    operator_id BIGINT COMMENT '操作人ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT '入库单表';

-- 入库单明细表
CREATE TABLE IF NOT EXISTS biz_in_stock_detail (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    quantity DECIMAL(10,2) COMMENT '数量',
    unit_price DECIMAL(10,2) COMMENT '单价',
    total_price DECIMAL(10,2) COMMENT '总价',
    remark VARCHAR(500) COMMENT '备注',
    in_stock_id BIGINT COMMENT '入库单ID',
    material_id BIGINT COMMENT '物资ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT '入库单明细表';

-- 出库单表
CREATE TABLE IF NOT EXISTS biz_out_stock (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_no VARCHAR(50) NOT NULL UNIQUE COMMENT '出库单号',
    receiver VARCHAR(100) COMMENT '接收人',
    receiver_phone VARCHAR(20) COMMENT '接收人电话',
    receiver_address VARCHAR(200) COMMENT '接收人地址',
    type INT COMMENT '出库类型 1:领用出库 2:退货出库 3:调拨出库',
    out_stock_time DATETIME COMMENT '出库时间',
    remark VARCHAR(500) COMMENT '备注',
    status INT DEFAULT 1 COMMENT '状态 1:已完成 0:草稿',
    warehouse_id BIGINT COMMENT '仓库ID',
    operator_id BIGINT COMMENT '操作人ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT '出库单表';

-- 出库单明细表
CREATE TABLE IF NOT EXISTS biz_out_stock_detail (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    quantity DECIMAL(10,2) COMMENT '数量',
    unit_price DECIMAL(10,2) COMMENT '单价',
    total_price DECIMAL(10,2) COMMENT '总价',
    remark VARCHAR(500) COMMENT '备注',
    out_stock_id BIGINT COMMENT '出库单ID',
    material_id BIGINT COMMENT '物资ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT '出库单明细表';

-- 库存表
CREATE TABLE IF NOT EXISTS biz_inventory (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    quantity DECIMAL(10,2) COMMENT '库存数量',
    unit_price DECIMAL(10,2) COMMENT '单价',
    total_price DECIMAL(10,2) COMMENT '库存总价',
    warehouse_id BIGINT COMMENT '仓库ID',
    material_id BIGINT COMMENT '物资ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_warehouse_material (warehouse_id, material_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT '库存表';

-- =====================================================
-- 初始化默认数据
-- =====================================================

-- 默认管理员用户（密码: admin123）
INSERT IGNORE INTO sys_user (username, password, real_name, phone, email, status) 
VALUES ('admin', '$2a$10$ZcWXl1tjbA/.R9ZxWX9XjO1RqC7pYlXzWxXzXyXzWxXyXzWxXyXzWx', '管理员', '13800138000', 'admin@warehouse.com', 1);

-- 默认角色
INSERT IGNORE INTO sys_role (name, code, description, status) 
VALUES ('超级管理员', 'ADMIN', '系统超级管理员角色', 1);

-- 用户角色关联
INSERT IGNORE INTO sys_user_role (user_id, role_id) 
SELECT u.id, r.id FROM sys_user u, sys_role r WHERE u.username = 'admin' AND r.code = 'ADMIN';

-- 默认权限
INSERT IGNORE INTO sys_permission (name, code, url, type, sort, status) VALUES
('用户管理', 'USER_MANAGE', '/api/users/**', 1, 1, 1),
('角色管理', 'ROLE_MANAGE', '/api/roles/**', 1, 2, 1),
('权限管理', 'PERMISSION_MANAGE', '/api/permissions/**', 1, 3, 1),
('部门管理', 'DEPT_MANAGE', '/api/departments/**', 1, 4, 1),
('机构管理', 'ORG_MANAGE', '/api/organizations/**', 1, 5, 1),
('仓库管理', 'WAREHOUSE_MANAGE', '/api/warehouses/**', 1, 6, 1),
('物资管理', 'MATERIAL_MANAGE', '/api/materials/**', 1, 7, 1),
('分类管理', 'CATEGORY_MANAGE', '/api/material-categories/**', 1, 8, 1),
('入库管理', 'INSTOCK_MANAGE', '/api/in-stock/**', 1, 9, 1),
('出库管理', 'OUTSTOCK_MANAGE', '/api/out-stock/**', 1, 10, 1),
('库存查询', 'INVENTORY_QUERY', '/api/inventory/**', 1, 11, 1);

-- 角色权限关联
INSERT IGNORE INTO sys_role_permission (role_id, permission_id)
SELECT r.id, p.id FROM sys_role r, sys_permission p WHERE r.code = 'ADMIN';
