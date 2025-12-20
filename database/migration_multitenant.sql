-- 多租户支持数据库迁移脚本
-- 支持 MySQL 5.7+ / PostgreSQL 10+

-- ========== 新增多租户相关表 ==========

-- 租户表
CREATE TABLE tenants (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(200) NOT NULL,
  code VARCHAR(50) UNIQUE NOT NULL,
  status ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_code (code),
  INDEX idx_status (status)
);

-- 用户表（用于登录）
CREATE TABLE users (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  phone VARCHAR(50) UNIQUE NOT NULL,
  name VARCHAR(100),
  email VARCHAR(100),
  password VARCHAR(255),
  employee_id BIGINT,
  status ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_phone (phone),
  INDEX idx_employee_id (employee_id),
  INDEX idx_status (status)
);

-- 用户租户关联表
CREATE TABLE user_tenants (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  tenant_id BIGINT NOT NULL,
  is_default BOOLEAN NOT NULL DEFAULT FALSE,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  FOREIGN KEY (tenant_id) REFERENCES tenants(id) ON DELETE CASCADE,
  UNIQUE KEY uk_user_tenant (user_id, tenant_id),
  INDEX idx_user_id (user_id),
  INDEX idx_tenant_id (tenant_id),
  INDEX idx_is_default (is_default)
);

-- ========== 为业务表添加 tenant_id 字段 ==========

-- 商品相关表
ALTER TABLE products ADD COLUMN tenant_id BIGINT NOT NULL DEFAULT 1 AFTER id;
ALTER TABLE products ADD INDEX idx_tenant_id (tenant_id);

ALTER TABLE colors ADD COLUMN tenant_id BIGINT NOT NULL DEFAULT 1 AFTER id;
ALTER TABLE colors ADD INDEX idx_tenant_id (tenant_id);

ALTER TABLE batches ADD COLUMN tenant_id BIGINT NOT NULL DEFAULT 1 AFTER id;
ALTER TABLE batches ADD INDEX idx_tenant_id (tenant_id);

-- 往来单位表
ALTER TABLE customers ADD COLUMN tenant_id BIGINT NOT NULL DEFAULT 1 AFTER id;
ALTER TABLE customers ADD INDEX idx_tenant_id (tenant_id);

ALTER TABLE suppliers ADD COLUMN tenant_id BIGINT NOT NULL DEFAULT 1 AFTER id;
ALTER TABLE suppliers ADD INDEX idx_tenant_id (tenant_id);

-- 进货单相关表
ALTER TABLE purchase_orders ADD COLUMN tenant_id BIGINT NOT NULL DEFAULT 1 AFTER id;
ALTER TABLE purchase_orders ADD INDEX idx_tenant_id (tenant_id);

ALTER TABLE purchase_order_items ADD COLUMN tenant_id BIGINT NOT NULL DEFAULT 1 AFTER id;
ALTER TABLE purchase_order_items ADD INDEX idx_tenant_id (tenant_id);

-- 销售单相关表
ALTER TABLE sales_orders ADD COLUMN tenant_id BIGINT NOT NULL DEFAULT 1 AFTER id;
ALTER TABLE sales_orders ADD INDEX idx_tenant_id (tenant_id);

ALTER TABLE sales_order_items ADD COLUMN tenant_id BIGINT NOT NULL DEFAULT 1 AFTER id;
ALTER TABLE sales_order_items ADD INDEX idx_tenant_id (tenant_id);

-- 染色加工单相关表
ALTER TABLE dyeing_orders ADD COLUMN tenant_id BIGINT NOT NULL DEFAULT 1 AFTER id;
ALTER TABLE dyeing_orders ADD INDEX idx_tenant_id (tenant_id);

ALTER TABLE dyeing_order_items ADD COLUMN tenant_id BIGINT NOT NULL DEFAULT 1 AFTER id;
ALTER TABLE dyeing_order_items ADD INDEX idx_tenant_id (tenant_id);

-- 账款相关表
ALTER TABLE account_receivables ADD COLUMN tenant_id BIGINT NOT NULL DEFAULT 1 AFTER id;
ALTER TABLE account_receivables ADD INDEX idx_tenant_id (tenant_id);

ALTER TABLE receipt_records ADD COLUMN tenant_id BIGINT NOT NULL DEFAULT 1 AFTER id;
ALTER TABLE receipt_records ADD INDEX idx_tenant_id (tenant_id);

ALTER TABLE account_payables ADD COLUMN tenant_id BIGINT NOT NULL DEFAULT 1 AFTER id;
ALTER TABLE account_payables ADD INDEX idx_tenant_id (tenant_id);

ALTER TABLE payment_records ADD COLUMN tenant_id BIGINT NOT NULL DEFAULT 1 AFTER id;
ALTER TABLE payment_records ADD INDEX idx_tenant_id (tenant_id);

-- 库存相关表
ALTER TABLE adjustment_orders ADD COLUMN tenant_id BIGINT NOT NULL DEFAULT 1 AFTER id;
ALTER TABLE adjustment_orders ADD INDEX idx_tenant_id (tenant_id);

ALTER TABLE adjustment_order_items ADD COLUMN tenant_id BIGINT NOT NULL DEFAULT 1 AFTER id;
ALTER TABLE adjustment_order_items ADD INDEX idx_tenant_id (tenant_id);

ALTER TABLE inventory_check_orders ADD COLUMN tenant_id BIGINT NOT NULL DEFAULT 1 AFTER id;
ALTER TABLE inventory_check_orders ADD INDEX idx_tenant_id (tenant_id);

ALTER TABLE inventory_check_items ADD COLUMN tenant_id BIGINT NOT NULL DEFAULT 1 AFTER id;
ALTER TABLE inventory_check_items ADD INDEX idx_tenant_id (tenant_id);

-- ========== 初始化数据 ==========

-- 创建默认租户
INSERT INTO tenants (name, code, status) VALUES ('默认租户', 'DEFAULT', 'ACTIVE');

-- 注意：如果需要移除默认值，执行以下语句（在确认所有数据都有正确的tenant_id后）
-- ALTER TABLE products ALTER COLUMN tenant_id DROP DEFAULT;
-- （对所有表重复此操作）
