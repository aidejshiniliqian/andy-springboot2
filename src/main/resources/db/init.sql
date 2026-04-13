CREATE DATABASE IF NOT EXISTS warehouse_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE warehouse_db;

SET NAMES utf8mb4;

-- 组织机构表
CREATE TABLE IF NOT EXISTS sys_organization (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL COMMENT '组织名称',
    code VARCHAR(50) COMMENT '组织编码',
    description VARCHAR(500) COMMENT '描述',
    status INT DEFAULT 1 COMMENT '状态：1启用，0禁用',
    parent_id BIGINT COMMENT '父级ID',
    created_at DATETIME COMMENT '创建时间',
    created_by VARCHAR(50) COMMENT '创建人',
    updated_at DATETIME COMMENT '更新时间',
    updated_by VARCHAR(50) COMMENT '更新人',
    deleted TINYINT(1) DEFAULT 0 COMMENT '是否删除',
    INDEX idx_parent_id (parent_id),
    INDEX idx_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='组织机构表';

-- 部门表
CREATE TABLE IF NOT EXISTS sys_department (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL COMMENT '部门名称',
    code VARCHAR(50) COMMENT '部门编码',
    description VARCHAR(500) COMMENT '描述',
    status INT DEFAULT 1 COMMENT '状态：1启用，0禁用',
    org_id BIGINT COMMENT '组织ID',
    parent_id BIGINT COMMENT '父级ID',
    created_at DATETIME COMMENT '创建时间',
    created_by VARCHAR(50) COMMENT '创建人',
    updated_at DATETIME COMMENT '更新时间',
    updated_by VARCHAR(50) COMMENT '更新人',
    deleted TINYINT(1) DEFAULT 0 COMMENT '是否删除',
    INDEX idx_org_id (org_id),
    INDEX idx_parent_id (parent_id),
    INDEX idx_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门表';

-- 权限表
CREATE TABLE IF NOT EXISTS sys_permission (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL COMMENT '权限名称',
    code VARCHAR(100) COMMENT '权限编码',
    type INT NOT NULL COMMENT '类型：1菜单，2按钮',
    parent_id BIGINT COMMENT '父级ID',
    path VARCHAR(255) COMMENT '路由路径',
    component VARCHAR(255) COMMENT '组件路径',
    icon VARCHAR(100) COMMENT '图标',
    sort_order INT DEFAULT 0 COMMENT '排序',
    status INT DEFAULT 1 COMMENT '状态：1启用，0禁用',
    created_at DATETIME COMMENT '创建时间',
    created_by VARCHAR(50) COMMENT '创建人',
    updated_at DATETIME COMMENT '更新时间',
    updated_by VARCHAR(50) COMMENT '更新人',
    deleted TINYINT(1) DEFAULT 0 COMMENT '是否删除',
    INDEX idx_parent_id (parent_id),
    INDEX idx_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

-- 角色表
CREATE TABLE IF NOT EXISTS sys_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL COMMENT '角色名称',
    code VARCHAR(50) COMMENT '角色编码',
    description VARCHAR(500) COMMENT '描述',
    status INT DEFAULT 1 COMMENT '状态：1启用，0禁用',
    created_at DATETIME COMMENT '创建时间',
    created_by VARCHAR(50) COMMENT '创建人',
    updated_at DATETIME COMMENT '更新时间',
    updated_by VARCHAR(50) COMMENT '更新人',
    deleted TINYINT(1) DEFAULT 0 COMMENT '是否删除',
    INDEX idx_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    real_name VARCHAR(50) COMMENT '真实姓名',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '电话',
    avatar VARCHAR(255) COMMENT '头像',
    status INT DEFAULT 1 COMMENT '状态：1启用，0禁用',
    org_id BIGINT COMMENT '组织ID',
    dept_id BIGINT COMMENT '部门ID',
    created_at DATETIME COMMENT '创建时间',
    created_by VARCHAR(50) COMMENT '创建人',
    updated_at DATETIME COMMENT '更新时间',
    updated_by VARCHAR(50) COMMENT '更新人',
    deleted TINYINT(1) DEFAULT 0 COMMENT '是否删除',
    UNIQUE KEY uk_username (username),
    INDEX idx_org_id (org_id),
    INDEX idx_dept_id (dept_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 用户角色关联表
CREATE TABLE IF NOT EXISTS sys_user_role (
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    PRIMARY KEY (user_id, role_id),
    INDEX idx_role_id (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 角色权限关联表
CREATE TABLE IF NOT EXISTS sys_role_permission (
    role_id BIGINT NOT NULL COMMENT '角色ID',
    permission_id BIGINT NOT NULL COMMENT '权限ID',
    PRIMARY KEY (role_id, permission_id),
    INDEX idx_permission_id (permission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';

-- 仓库表
CREATE TABLE IF NOT EXISTS wh_warehouse (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL COMMENT '仓库名称',
    code VARCHAR(50) COMMENT '仓库编码',
    address VARCHAR(255) COMMENT '地址',
    area DECIMAL(10,2) COMMENT '面积',
    capacity INT COMMENT '容量',
    manager_id BIGINT COMMENT '负责人ID',
    manager_name VARCHAR(50) COMMENT '负责人姓名',
    phone VARCHAR(20) COMMENT '电话',
    description VARCHAR(500) COMMENT '描述',
    status INT DEFAULT 1 COMMENT '状态：1启用，0禁用',
    org_id BIGINT COMMENT '组织ID',
    created_at DATETIME COMMENT '创建时间',
    created_by VARCHAR(50) COMMENT '创建人',
    updated_at DATETIME COMMENT '更新时间',
    updated_by VARCHAR(50) COMMENT '更新人',
    deleted TINYINT(1) DEFAULT 0 COMMENT '是否删除',
    UNIQUE KEY uk_code (code),
    INDEX idx_org_id (org_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='仓库表';

-- 物资分类表
CREATE TABLE IF NOT EXISTS wh_material_category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL COMMENT '分类名称',
    code VARCHAR(50) COMMENT '分类编码',
    description VARCHAR(500) COMMENT '描述',
    status INT DEFAULT 1 COMMENT '状态：1启用，0禁用',
    parent_id BIGINT COMMENT '父级ID',
    created_at DATETIME COMMENT '创建时间',
    created_by VARCHAR(50) COMMENT '创建人',
    updated_at DATETIME COMMENT '更新时间',
    updated_by VARCHAR(50) COMMENT '更新人',
    deleted TINYINT(1) DEFAULT 0 COMMENT '是否删除',
    UNIQUE KEY uk_code (code),
    INDEX idx_parent_id (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物资分类表';

-- 物资表
CREATE TABLE IF NOT EXISTS wh_material (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL COMMENT '物资名称',
    code VARCHAR(50) COMMENT '物资编码',
    barcode VARCHAR(100) COMMENT '条码',
    specification VARCHAR(200) COMMENT '规格',
    model VARCHAR(100) COMMENT '型号',
    unit VARCHAR(20) COMMENT '单位',
    price DECIMAL(10,2) COMMENT '单价',
    safety_stock INT COMMENT '安全库存',
    max_stock INT COMMENT '最大库存',
    description VARCHAR(500) COMMENT '描述',
    status INT DEFAULT 1 COMMENT '状态：1启用，0禁用',
    category_id BIGINT COMMENT '分类ID',
    created_at DATETIME COMMENT '创建时间',
    created_by VARCHAR(50) COMMENT '创建人',
    updated_at DATETIME COMMENT '更新时间',
    updated_by VARCHAR(50) COMMENT '更新人',
    deleted TINYINT(1) DEFAULT 0 COMMENT '是否删除',
    UNIQUE KEY uk_code (code),
    INDEX idx_barcode (barcode),
    INDEX idx_category_id (category_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物资表';

-- 库存表
CREATE TABLE IF NOT EXISTS wh_stock (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    warehouse_id BIGINT NOT NULL COMMENT '仓库ID',
    material_id BIGINT NOT NULL COMMENT '物资ID',
    quantity INT NOT NULL DEFAULT 0 COMMENT '库存数量',
    available_quantity INT NOT NULL DEFAULT 0 COMMENT '可用数量',
    locked_quantity INT NOT NULL DEFAULT 0 COMMENT '锁定数量',
    batch_no VARCHAR(50) COMMENT '批次号',
    position VARCHAR(50) COMMENT '库位',
    created_at DATETIME COMMENT '创建时间',
    created_by VARCHAR(50) COMMENT '创建人',
    updated_at DATETIME COMMENT '更新时间',
    updated_by VARCHAR(50) COMMENT '更新人',
    deleted TINYINT(1) DEFAULT 0 COMMENT '是否删除',
    UNIQUE KEY uk_warehouse_material (warehouse_id, material_id),
    INDEX idx_material_id (material_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存表';

-- 入库单表
CREATE TABLE IF NOT EXISTS wh_stock_in_order (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_no VARCHAR(50) COMMENT '单据编号',
    order_type INT NOT NULL COMMENT '入库类型：1采购入库，2退货入库，3调拨入库',
    warehouse_id BIGINT NOT NULL COMMENT '仓库ID',
    supplier VARCHAR(100) COMMENT '供应商',
    total_amount DECIMAL(12,2) COMMENT '总金额',
    order_date DATETIME COMMENT '入库日期',
    status INT DEFAULT 0 COMMENT '状态：0待审核，1已审核，2已驳回',
    remark VARCHAR(500) COMMENT '备注',
    operator_id BIGINT COMMENT '操作人ID',
    operator_name VARCHAR(50) COMMENT '操作人姓名',
    created_at DATETIME COMMENT '创建时间',
    created_by VARCHAR(50) COMMENT '创建人',
    updated_at DATETIME COMMENT '更新时间',
    updated_by VARCHAR(50) COMMENT '更新人',
    deleted TINYINT(1) DEFAULT 0 COMMENT '是否删除',
    UNIQUE KEY uk_order_no (order_no),
    INDEX idx_warehouse_id (warehouse_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='入库单表';

-- 入库单明细表
CREATE TABLE IF NOT EXISTS wh_stock_in_order_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL COMMENT '入库单ID',
    material_id BIGINT NOT NULL COMMENT '物资ID',
    quantity INT NOT NULL COMMENT '数量',
    unit_price DECIMAL(10,2) COMMENT '单价',
    total_price DECIMAL(12,2) COMMENT '总价',
    batch_no VARCHAR(50) COMMENT '批次号',
    position VARCHAR(50) COMMENT '库位',
    remark VARCHAR(255) COMMENT '备注',
    INDEX idx_order_id (order_id),
    INDEX idx_material_id (material_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='入库单明细表';

-- 出库单表
CREATE TABLE IF NOT EXISTS wh_stock_out_order (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_no VARCHAR(50) COMMENT '单据编号',
    order_type INT NOT NULL COMMENT '出库类型：1领用出库，2销售出库，3调拨出库',
    warehouse_id BIGINT NOT NULL COMMENT '仓库ID',
    receiver VARCHAR(100) COMMENT '领用人/客户',
    total_amount DECIMAL(12,2) COMMENT '总金额',
    order_date DATETIME COMMENT '出库日期',
    status INT DEFAULT 0 COMMENT '状态：0待审核，1已审核，2已驳回',
    remark VARCHAR(500) COMMENT '备注',
    operator_id BIGINT COMMENT '操作人ID',
    operator_name VARCHAR(50) COMMENT '操作人姓名',
    created_at DATETIME COMMENT '创建时间',
    created_by VARCHAR(50) COMMENT '创建人',
    updated_at DATETIME COMMENT '更新时间',
    updated_by VARCHAR(50) COMMENT '更新人',
    deleted TINYINT(1) DEFAULT 0 COMMENT '是否删除',
    UNIQUE KEY uk_order_no (order_no),
    INDEX idx_warehouse_id (warehouse_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='出库单表';

-- 出库单明细表
CREATE TABLE IF NOT EXISTS wh_stock_out_order_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL COMMENT '出库单ID',
    material_id BIGINT NOT NULL COMMENT '物资ID',
    quantity INT NOT NULL COMMENT '数量',
    unit_price DECIMAL(10,2) COMMENT '单价',
    total_price DECIMAL(12,2) COMMENT '总价',
    batch_no VARCHAR(50) COMMENT '批次号',
    remark VARCHAR(255) COMMENT '备注',
    INDEX idx_order_id (order_id),
    INDEX idx_material_id (material_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='出库单明细表';

-- 初始化数据
-- 插入默认组织
INSERT INTO sys_organization (id, name, code, description, status, created_at, deleted) VALUES
(1, '总公司', 'HQ', '总部组织', 1, NOW(), 0);

-- 插入默认部门
INSERT INTO sys_department (id, name, code, description, status, org_id, created_at, deleted) VALUES
(1, '总经办', 'GM', '总经理办公室', 1, 1, NOW(), 0),
(2, '仓储部', 'WH', '仓储管理部门', 1, 1, NOW(), 0),
(3, '采购部', 'PUR', '采购部门', 1, 1, NOW(), 0);

-- 插入默认权限
INSERT INTO sys_permission (id, name, code, type, parent_id, path, component, icon, sort_order, status, created_at, deleted) VALUES
(1, '系统管理', 'system', 1, NULL, '/system', 'Layout', 'setting', 1, 1, NOW(), 0),
(2, '用户管理', 'user:view', 2, 1, '/system/users', 'system/user/index', 'user', 1, 1, NOW(), 0),
(3, '用户创建', 'user:create', 2, 2, NULL, NULL, NULL, 1, 1, NOW(), 0),
(4, '用户编辑', 'user:update', 2, 2, NULL, NULL, NULL, 2, 1, NOW(), 0),
(5, '用户删除', 'user:delete', 2, 2, NULL, NULL, NULL, 3, 1, NOW(), 0),
(6, '角色管理', 'role:view', 2, 1, '/system/roles', 'system/role/index', 'peoples', 2, 1, NOW(), 0),
(7, '角色创建', 'role:create', 2, 6, NULL, NULL, NULL, 1, 1, NOW(), 0),
(8, '角色编辑', 'role:update', 2, 6, NULL, NULL, NULL, 2, 1, NOW(), 0),
(9, '角色删除', 'role:delete', 2, 6, NULL, NULL, NULL, 3, 1, NOW(), 0),
(10, '权限管理', 'perm:view', 2, 1, '/system/permissions', 'system/permission/index', 'lock', 3, 1, NOW(), 0),
(11, '权限创建', 'perm:create', 2, 10, NULL, NULL, NULL, 1, 1, NOW(), 0),
(12, '权限编辑', 'perm:update', 2, 10, NULL, NULL, NULL, 2, 1, NOW(), 0),
(13, '权限删除', 'perm:delete', 2, 10, NULL, NULL, NULL, 3, 1, NOW(), 0),
(14, '组织管理', 'org:view', 2, 1, '/system/organizations', 'system/org/index', 'tree', 4, 1, NOW(), 0),
(15, '组织创建', 'org:create', 2, 14, NULL, NULL, NULL, 1, 1, NOW(), 0),
(16, '组织编辑', 'org:update', 2, 14, NULL, NULL, NULL, 2, 1, NOW(), 0),
(17, '组织删除', 'org:delete', 2, 14, NULL, NULL, NULL, 3, 1, NOW(), 0),
(18, '部门管理', 'dept:view', 2, 1, '/system/departments', 'system/dept/index', 'tree-table', 5, 1, NOW(), 0),
(19, '部门创建', 'dept:create', 2, 18, NULL, NULL, NULL, 1, 1, NOW(), 0),
(20, '部门编辑', 'dept:update', 2, 18, NULL, NULL, NULL, 2, 1, NOW(), 0),
(21, '部门删除', 'dept:delete', 2, 18, NULL, NULL, NULL, 3, 1, NOW(), 0),
(22, '仓库管理', 'warehouse', 1, NULL, '/warehouse', 'Layout', 'shopping', 2, 1, NOW(), 0),
(23, '仓库列表', 'warehouse:view', 2, 22, '/warehouse/list', 'warehouse/list/index', 'list', 1, 1, NOW(), 0),
(24, '仓库创建', 'warehouse:create', 2, 23, NULL, NULL, NULL, 1, 1, NOW(), 0),
(25, '仓库编辑', 'warehouse:update', 2, 23, NULL, NULL, NULL, 2, 1, NOW(), 0),
(26, '仓库删除', 'warehouse:delete', 2, 23, NULL, NULL, NULL, 3, 1, NOW(), 0),
(27, '物资管理', 'material', 1, NULL, '/material', 'Layout', 'shopping', 3, 1, NOW(), 0),
(28, '物资列表', 'material:view', 2, 27, '/material/list', 'material/list/index', 'list', 1, 1, NOW(), 0),
(29, '物资创建', 'material:create', 2, 28, NULL, NULL, NULL, 1, 1, NOW(), 0),
(30, '物资编辑', 'material:update', 2, 28, NULL, NULL, NULL, 2, 1, NOW(), 0),
(31, '物资删除', 'material:delete', 2, 28, NULL, NULL, NULL, 3, 1, NOW(), 0),
(32, '库存管理', 'stock', 1, NULL, '/stock', 'Layout', 'component', 4, 1, NOW(), 0),
(33, '库存查询', 'stock:view', 2, 32, '/stock/list', 'stock/list/index', 'list', 1, 1, NOW(), 0),
(34, '入库管理', 'stock:create', 2, 32, '/stock/in', 'stock/in/index', 'edit', 2, 1, NOW(), 0),
(35, '出库管理', 'stock:create', 2, 32, '/stock/out', 'stock/out/index', 'edit', 3, 1, NOW(), 0),
(36, '库存审核', 'stock:approve', 2, 32, NULL, NULL, NULL, 4, 1, NOW(), 0),
(37, '库存删除', 'stock:delete', 2, 32, NULL, NULL, NULL, 5, 1, NOW(), 0);

-- 插入默认角色
INSERT INTO sys_role (id, name, code, description, status, created_at, deleted) VALUES
(1, '超级管理员', 'SUPER_ADMIN', '拥有所有权限', 1, NOW(), 0),
(2, '仓库管理员', 'WAREHOUSE_ADMIN', '仓库管理权限', 1, NOW(), 0),
(3, '普通用户', 'USER', '普通用户权限', 1, NOW(), 0);

-- 插入角色权限关联
INSERT INTO sys_role_permission (role_id, permission_id) VALUES
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6), (1, 7), (1, 8), (1, 9),
(1, 10), (1, 11), (1, 12), (1, 13), (1, 14), (1, 15), (1, 16), (1, 17),
(1, 18), (1, 19), (1, 20), (1, 21), (1, 22), (1, 23), (1, 24), (1, 25),
(1, 26), (1, 27), (1, 28), (1, 29), (1, 30), (1, 31), (1, 32), (1, 33),
(1, 34), (1, 35), (1, 36), (1, 37),
(2, 22), (2, 23), (2, 24), (2, 25), (2, 26), (2, 27), (2, 28), (2, 29),
(2, 30), (2, 31), (2, 32), (2, 33), (2, 34), (2, 35), (2, 36), (2, 37),
(3, 22), (3, 23), (3, 27), (3, 28), (3, 32), (3, 33);

-- 插入默认管理员用户 (密码: admin123)
INSERT INTO sys_user (id, username, password, real_name, email, phone, status, org_id, dept_id, created_at, deleted) VALUES
(1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '系统管理员', 'admin@example.com', '13800138000', 1, 1, 1, NOW(), 0);

-- 插入用户角色关联
INSERT INTO sys_user_role (user_id, role_id) VALUES (1, 1);

-- 插入默认仓库
INSERT INTO wh_warehouse (id, name, code, address, status, org_id, created_at, deleted) VALUES
(1, '主仓库', 'WH001', '总部一楼', 1, 1, NOW(), 0);

-- 插入物资分类
INSERT INTO wh_material_category (id, name, code, description, status, created_at, deleted) VALUES
(1, '办公用品', 'OFFICE', '办公用品分类', 1, NOW(), 0),
(2, '电子设备', 'ELECTRONIC', '电子设备分类', 1, NOW(), 0),
(3, '劳保用品', 'SAFETY', '劳保用品分类', 1, NOW(), 0);

-- 插入示例物资
INSERT INTO wh_material (id, name, code, barcode, specification, unit, price, status, category_id, created_at, deleted) VALUES
(1, 'A4打印纸', 'MAT001', '6901234567890', '70g/500张', '包', 25.00, 1, 1, NOW(), 0),
(2, '签字笔', 'MAT002', '6901234567891', '0.5mm黑色', '支', 2.00, 1, 1, NOW(), 0),
(3, '笔记本电脑', 'MAT003', '6901234567892', '14寸 i7 16G 512G', '台', 6000.00, 1, 2, NOW(), 0),
(4, '安全帽', 'MAT004', '6901234567893', '白色', '个', 30.00, 1, 3, NOW(), 0);
