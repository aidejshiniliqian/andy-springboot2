-- 仓库物资管理系统数据库脚本
-- 适用于 MySQL 8.0+

-- 创建数据库
CREATE DATABASE IF NOT EXISTS warehouse_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE warehouse_db;

-- 系统组织机构表
CREATE TABLE IF NOT EXISTS sys_organization (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    org_code VARCHAR(50) NOT NULL UNIQUE COMMENT '机构编码',
    org_name VARCHAR(100) NOT NULL COMMENT '机构名称',
    description VARCHAR(500) COMMENT '描述',
    address VARCHAR(200) COMMENT '地址',
    contact_person VARCHAR(50) COMMENT '联系人',
    contact_phone VARCHAR(20) COMMENT '联系电话',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME,
    created_by BIGINT,
    updated_by BIGINT,
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
    INDEX idx_org_code (org_code),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统组织机构表';

-- 系统部门表
CREATE TABLE IF NOT EXISTS sys_department (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    dept_code VARCHAR(50) NOT NULL COMMENT '部门编码',
    dept_name VARCHAR(100) NOT NULL COMMENT '部门名称',
    description VARCHAR(500) COMMENT '描述',
    parent_id BIGINT COMMENT '父部门ID',
    org_id BIGINT NOT NULL COMMENT '所属机构ID',
    sort_order INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME,
    created_by BIGINT,
    updated_by BIGINT,
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
    INDEX idx_dept_code (dept_code),
    INDEX idx_org_id (org_id),
    INDEX idx_parent_id (parent_id),
    INDEX idx_status (status),
    FOREIGN KEY (parent_id) REFERENCES sys_department(id),
    FOREIGN KEY (org_id) REFERENCES sys_organization(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统部门表';

-- 系统权限表
CREATE TABLE IF NOT EXISTS sys_permission (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    permission_code VARCHAR(50) NOT NULL UNIQUE COMMENT '权限编码',
    permission_name VARCHAR(100) NOT NULL COMMENT '权限名称',
    description VARCHAR(500) COMMENT '描述',
    type VARCHAR(20) COMMENT '类型: MENU-菜单, BUTTON-按钮, API-接口',
    resource_url VARCHAR(200) COMMENT '资源URL',
    http_method VARCHAR(10) COMMENT 'HTTP方法',
    parent_id BIGINT COMMENT '父权限ID',
    sort_order INT DEFAULT 0 COMMENT '排序',
    icon VARCHAR(50) COMMENT '图标',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME,
    created_by BIGINT,
    updated_by BIGINT,
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
    INDEX idx_permission_code (permission_code),
    INDEX idx_parent_id (parent_id),
    INDEX idx_status (status),
    FOREIGN KEY (parent_id) REFERENCES sys_permission(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统权限表';

-- 系统角色表
CREATE TABLE IF NOT EXISTS sys_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_code VARCHAR(50) NOT NULL UNIQUE COMMENT '角色编码',
    role_name VARCHAR(100) NOT NULL COMMENT '角色名称',
    description VARCHAR(500) COMMENT '描述',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME,
    created_by BIGINT,
    updated_by BIGINT,
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
    INDEX idx_role_code (role_code),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统角色表';

-- 角色权限关联表
CREATE TABLE IF NOT EXISTS sys_role_permission (
    role_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES sys_role(id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES sys_permission(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';

-- 系统用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    real_name VARCHAR(50) COMMENT '真实姓名',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '电话',
    avatar VARCHAR(200) COMMENT '头像',
    gender TINYINT COMMENT '性别: 0-女, 1-男, 2-保密',
    org_id BIGINT COMMENT '所属机构ID',
    dept_id BIGINT COMMENT '所属部门ID',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    last_login_time DATETIME COMMENT '最后登录时间',
    last_login_ip VARCHAR(50) COMMENT '最后登录IP',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME,
    created_by BIGINT,
    updated_by BIGINT,
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
    INDEX idx_username (username),
    INDEX idx_org_id (org_id),
    INDEX idx_dept_id (dept_id),
    INDEX idx_status (status),
    FOREIGN KEY (org_id) REFERENCES sys_organization(id),
    FOREIGN KEY (dept_id) REFERENCES sys_department(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- 用户角色关联表
CREATE TABLE IF NOT EXISTS sys_user_role (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES sys_user(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES sys_role(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 仓库表
CREATE TABLE IF NOT EXISTS wms_warehouse (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    warehouse_code VARCHAR(50) NOT NULL UNIQUE COMMENT '仓库编码',
    warehouse_name VARCHAR(100) NOT NULL COMMENT '仓库名称',
    description VARCHAR(500) COMMENT '描述',
    address VARCHAR(200) COMMENT '地址',
    area DECIMAL(10, 2) COMMENT '面积(m²)',
    capacity DECIMAL(15, 2) COMMENT '容量',
    manager_id BIGINT COMMENT '负责人ID',
    contact_phone VARCHAR(20) COMMENT '联系电话',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME,
    created_by BIGINT,
    updated_by BIGINT,
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
    INDEX idx_warehouse_code (warehouse_code),
    INDEX idx_status (status),
    FOREIGN KEY (manager_id) REFERENCES sys_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='仓库表';

-- 仓库库区表
CREATE TABLE IF NOT EXISTS wms_warehouse_zone (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    zone_code VARCHAR(50) NOT NULL COMMENT '库区编码',
    zone_name VARCHAR(100) NOT NULL COMMENT '库区名称',
    zone_type VARCHAR(20) COMMENT '库区类型',
    warehouse_id BIGINT NOT NULL COMMENT '所属仓库ID',
    area DECIMAL(10, 2) COMMENT '面积(m²)',
    capacity DECIMAL(15, 2) COMMENT '容量',
    description VARCHAR(500) COMMENT '描述',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME,
    created_by BIGINT,
    updated_by BIGINT,
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
    INDEX idx_zone_code (zone_code),
    INDEX idx_warehouse_id (warehouse_id),
    INDEX idx_status (status),
    FOREIGN KEY (warehouse_id) REFERENCES wms_warehouse(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='仓库库区表';

-- 仓库库位表
CREATE TABLE IF NOT EXISTS wms_warehouse_location (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    location_code VARCHAR(50) NOT NULL COMMENT '库位编码',
    location_name VARCHAR(100) NOT NULL COMMENT '库位名称',
    zone_id BIGINT NOT NULL COMMENT '所属库区ID',
    warehouse_id BIGINT NOT NULL COMMENT '所属仓库ID',
    capacity DECIMAL(15, 2) COMMENT '容量',
    length DECIMAL(10, 2) COMMENT '长(m)',
    width DECIMAL(10, 2) COMMENT '宽(m)',
    height DECIMAL(10, 2) COMMENT '高(m)',
    max_weight DECIMAL(10, 2) COMMENT '最大承重(kg)',
    description VARCHAR(500) COMMENT '描述',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME,
    created_by BIGINT,
    updated_by BIGINT,
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
    INDEX idx_location_code (location_code),
    INDEX idx_zone_id (zone_id),
    INDEX idx_warehouse_id (warehouse_id),
    INDEX idx_status (status),
    FOREIGN KEY (zone_id) REFERENCES wms_warehouse_zone(id),
    FOREIGN KEY (warehouse_id) REFERENCES wms_warehouse(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='仓库库位表';

-- 物资分类表
CREATE TABLE IF NOT EXISTS wms_material_category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    category_code VARCHAR(50) NOT NULL COMMENT '分类编码',
    category_name VARCHAR(100) NOT NULL COMMENT '分类名称',
    description VARCHAR(500) COMMENT '描述',
    parent_id BIGINT COMMENT '父分类ID',
    sort_order INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME,
    created_by BIGINT,
    updated_by BIGINT,
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
    INDEX idx_category_code (category_code),
    INDEX idx_parent_id (parent_id),
    INDEX idx_status (status),
    FOREIGN KEY (parent_id) REFERENCES wms_material_category(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物资分类表';

-- 物资表
CREATE TABLE IF NOT EXISTS wms_material (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    material_code VARCHAR(50) NOT NULL UNIQUE COMMENT '物资编码',
    material_name VARCHAR(100) NOT NULL COMMENT '物资名称',
    specification VARCHAR(200) COMMENT '规格',
    model VARCHAR(50) COMMENT '型号',
    category_id BIGINT COMMENT '分类ID',
    unit VARCHAR(20) COMMENT '单位',
    barcode VARCHAR(50) COMMENT '条码',
    purchase_price DECIMAL(15, 2) COMMENT '采购价',
    sale_price DECIMAL(15, 2) COMMENT '销售价',
    safety_stock DECIMAL(15, 2) COMMENT '安全库存',
    max_stock DECIMAL(15, 2) COMMENT '最大库存',
    description VARCHAR(500) COMMENT '描述',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME,
    created_by BIGINT,
    updated_by BIGINT,
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
    INDEX idx_material_code (material_code),
    INDEX idx_barcode (barcode),
    INDEX idx_category_id (category_id),
    INDEX idx_status (status),
    FOREIGN KEY (category_id) REFERENCES wms_material_category(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物资表';

-- 库存表
CREATE TABLE IF NOT EXISTS wms_inventory (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    material_id BIGINT NOT NULL COMMENT '物资ID',
    warehouse_id BIGINT NOT NULL COMMENT '仓库ID',
    location_id BIGINT COMMENT '库位ID',
    quantity DECIMAL(15, 2) NOT NULL DEFAULT 0 COMMENT '数量',
    available_quantity DECIMAL(15, 2) DEFAULT 0 COMMENT '可用数量',
    locked_quantity DECIMAL(15, 2) DEFAULT 0 COMMENT '锁定数量',
    unit VARCHAR(20) COMMENT '单位',
    batch_no VARCHAR(50) COMMENT '批次号',
    production_date DATE COMMENT '生产日期',
    expiry_date DATE COMMENT '过期日期',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME,
    created_by BIGINT,
    updated_by BIGINT,
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
    INDEX idx_material_id (material_id),
    INDEX idx_warehouse_id (warehouse_id),
    INDEX idx_location_id (location_id),
    INDEX idx_batch_no (batch_no),
    INDEX idx_status (status),
    FOREIGN KEY (material_id) REFERENCES wms_material(id),
    FOREIGN KEY (warehouse_id) REFERENCES wms_warehouse(id),
    FOREIGN KEY (location_id) REFERENCES wms_warehouse_location(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存表';

-- 入库单表
CREATE TABLE IF NOT EXISTS wms_stock_in_order (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_no VARCHAR(50) NOT NULL UNIQUE COMMENT '入库单号',
    order_type VARCHAR(20) COMMENT '入库类型',
    warehouse_id BIGINT NOT NULL COMMENT '仓库ID',
    supplier_name VARCHAR(100) COMMENT '供应商名称',
    supplier_contact VARCHAR(50) COMMENT '供应商联系人',
    supplier_phone VARCHAR(20) COMMENT '供应商电话',
    total_amount DECIMAL(15, 2) COMMENT '总金额',
    total_quantity DECIMAL(15, 2) COMMENT '总数量',
    order_date DATE COMMENT '单据日期',
    expected_date DATE COMMENT '预计到货日期',
    actual_date DATETIME COMMENT '实际入库时间',
    operator_id BIGINT COMMENT '操作人ID',
    remark VARCHAR(500) COMMENT '备注',
    status VARCHAR(20) DEFAULT 'PENDING' COMMENT '状态: PENDING-待确认, COMPLETED-已完成, CANCELLED-已取消',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME,
    created_by BIGINT,
    updated_by BIGINT,
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
    INDEX idx_order_no (order_no),
    INDEX idx_warehouse_id (warehouse_id),
    INDEX idx_status (status),
    INDEX idx_order_date (order_date),
    FOREIGN KEY (warehouse_id) REFERENCES wms_warehouse(id),
    FOREIGN KEY (operator_id) REFERENCES sys_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='入库单表';

-- 入库单明细表
CREATE TABLE IF NOT EXISTS wms_stock_in_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    stock_in_order_id BIGINT NOT NULL COMMENT '入库单ID',
    material_id BIGINT NOT NULL COMMENT '物资ID',
    location_id BIGINT COMMENT '库位ID',
    quantity DECIMAL(15, 2) NOT NULL COMMENT '数量',
    actual_quantity DECIMAL(15, 2) COMMENT '实际数量',
    unit VARCHAR(20) COMMENT '单位',
    unit_price DECIMAL(15, 2) COMMENT '单价',
    total_amount DECIMAL(15, 2) COMMENT '金额',
    batch_no VARCHAR(50) COMMENT '批次号',
    production_date DATE COMMENT '生产日期',
    expiry_date DATE COMMENT '过期日期',
    remark VARCHAR(200) COMMENT '备注',
    status VARCHAR(20) DEFAULT 'PENDING' COMMENT '状态',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME,
    created_by BIGINT,
    updated_by BIGINT,
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
    INDEX idx_stock_in_order_id (stock_in_order_id),
    INDEX idx_material_id (material_id),
    INDEX idx_location_id (location_id),
    FOREIGN KEY (stock_in_order_id) REFERENCES wms_stock_in_order(id),
    FOREIGN KEY (material_id) REFERENCES wms_material(id),
    FOREIGN KEY (location_id) REFERENCES wms_warehouse_location(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='入库单明细表';

-- 出库单表
CREATE TABLE IF NOT EXISTS wms_stock_out_order (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_no VARCHAR(50) NOT NULL UNIQUE COMMENT '出库单号',
    order_type VARCHAR(20) COMMENT '出库类型',
    warehouse_id BIGINT NOT NULL COMMENT '仓库ID',
    recipient_name VARCHAR(100) COMMENT '领用人',
    recipient_dept VARCHAR(100) COMMENT '领用部门',
    recipient_contact VARCHAR(50) COMMENT '联系人',
    recipient_phone VARCHAR(20) COMMENT '联系电话',
    total_amount DECIMAL(15, 2) COMMENT '总金额',
    total_quantity DECIMAL(15, 2) COMMENT '总数量',
    order_date DATE COMMENT '单据日期',
    expected_date DATE COMMENT '预计出库日期',
    actual_date DATETIME COMMENT '实际出库时间',
    operator_id BIGINT COMMENT '操作人ID',
    approver_id BIGINT COMMENT '审批人ID',
    approve_time DATETIME COMMENT '审批时间',
    remark VARCHAR(500) COMMENT '备注',
    status VARCHAR(20) DEFAULT 'PENDING' COMMENT '状态: PENDING-待审批, APPROVED-已审批, COMPLETED-已完成, CANCELLED-已取消',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME,
    created_by BIGINT,
    updated_by BIGINT,
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
    INDEX idx_order_no (order_no),
    INDEX idx_warehouse_id (warehouse_id),
    INDEX idx_status (status),
    INDEX idx_order_date (order_date),
    FOREIGN KEY (warehouse_id) REFERENCES wms_warehouse(id),
    FOREIGN KEY (operator_id) REFERENCES sys_user(id),
    FOREIGN KEY (approver_id) REFERENCES sys_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='出库单表';

-- 出库单明细表
CREATE TABLE IF NOT EXISTS wms_stock_out_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    stock_out_order_id BIGINT NOT NULL COMMENT '出库单ID',
    material_id BIGINT NOT NULL COMMENT '物资ID',
    location_id BIGINT COMMENT '库位ID',
    quantity DECIMAL(15, 2) NOT NULL COMMENT '数量',
    actual_quantity DECIMAL(15, 2) COMMENT '实际数量',
    unit VARCHAR(20) COMMENT '单位',
    unit_price DECIMAL(15, 2) COMMENT '单价',
    total_amount DECIMAL(15, 2) COMMENT '金额',
    batch_no VARCHAR(50) COMMENT '批次号',
    remark VARCHAR(200) COMMENT '备注',
    status VARCHAR(20) DEFAULT 'PENDING' COMMENT '状态',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME,
    created_by BIGINT,
    updated_by BIGINT,
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
    INDEX idx_stock_out_order_id (stock_out_order_id),
    INDEX idx_material_id (material_id),
    INDEX idx_location_id (location_id),
    FOREIGN KEY (stock_out_order_id) REFERENCES wms_stock_out_order(id),
    FOREIGN KEY (material_id) REFERENCES wms_material(id),
    FOREIGN KEY (location_id) REFERENCES wms_warehouse_location(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='出库单明细表';

-- 库存记录表
CREATE TABLE IF NOT EXISTS wms_inventory_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    record_no VARCHAR(50) NOT NULL UNIQUE COMMENT '记录编号',
    record_type VARCHAR(20) NOT NULL COMMENT '记录类型: IN-入库, OUT-出库',
    biz_type VARCHAR(20) COMMENT '业务类型',
    biz_no VARCHAR(50) COMMENT '业务单号',
    material_id BIGINT NOT NULL COMMENT '物资ID',
    warehouse_id BIGINT NOT NULL COMMENT '仓库ID',
    location_id BIGINT COMMENT '库位ID',
    quantity DECIMAL(15, 2) NOT NULL COMMENT '数量',
    before_quantity DECIMAL(15, 2) COMMENT '变更前数量',
    after_quantity DECIMAL(15, 2) COMMENT '变更后数量',
    unit VARCHAR(20) COMMENT '单位',
    batch_no VARCHAR(50) COMMENT '批次号',
    operator_id BIGINT COMMENT '操作人ID',
    remark VARCHAR(500) COMMENT '备注',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME,
    created_by BIGINT,
    updated_by BIGINT,
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
    INDEX idx_record_no (record_no),
    INDEX idx_record_type (record_type),
    INDEX idx_biz_no (biz_no),
    INDEX idx_material_id (material_id),
    INDEX idx_warehouse_id (warehouse_id),
    INDEX idx_created_at (created_at),
    FOREIGN KEY (material_id) REFERENCES wms_material(id),
    FOREIGN KEY (warehouse_id) REFERENCES wms_warehouse(id),
    FOREIGN KEY (location_id) REFERENCES wms_warehouse_location(id),
    FOREIGN KEY (operator_id) REFERENCES sys_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存记录表';
