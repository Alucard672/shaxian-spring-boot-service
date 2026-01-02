-- ========== 软件销售订单相关表 ==========

-- 修改 tenants 表，添加 crm_customer_id 字段
ALTER TABLE tenants ADD COLUMN crm_customer_id BIGINT NULL COMMENT '关联的CRM客户ID';
ALTER TABLE tenants ADD INDEX idx_crm_customer_id (crm_customer_id);

-- 软件销售订单表（全局管理，不分租户）
CREATE TABLE crm_sales_orders (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  order_number VARCHAR(50) UNIQUE NOT NULL COMMENT '订单号',
  crm_customer_id BIGINT NOT NULL COMMENT 'CRM客户ID',
  customer_name VARCHAR(200) NOT NULL COMMENT '客户名称（冗余字段）',
  sales_date DATE NOT NULL COMMENT '销售日期',
  total_amount DECIMAL(12, 2) NOT NULL DEFAULT 0 COMMENT '总金额',
  paid_amount DECIMAL(12, 2) NOT NULL DEFAULT 0 COMMENT '已付金额',
  unpaid_amount DECIMAL(12, 2) NOT NULL DEFAULT 0 COMMENT '未付金额',
  status ENUM('DRAFT', 'PAID', 'REVIEWED', 'CANCELLED') NOT NULL DEFAULT 'DRAFT' COMMENT '订单状态',
  operator VARCHAR(100) COMMENT '操作员',
  remark TEXT COMMENT '备注',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (crm_customer_id) REFERENCES crm_customers(id),
  INDEX idx_order_number (order_number),
  INDEX idx_crm_customer_id (crm_customer_id),
  INDEX idx_sales_date (sales_date),
  INDEX idx_status (status)
);

-- 软件销售订单明细表
CREATE TABLE crm_sales_order_items (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  order_id BIGINT NOT NULL COMMENT '订单ID',
  product_id BIGINT NOT NULL COMMENT '产品ID',
  product_name VARCHAR(200) NOT NULL COMMENT '产品名称（冗余）',
  product_code VARCHAR(50) NOT NULL COMMENT '产品编码（冗余）',
  unit_price DECIMAL(12, 2) NOT NULL COMMENT '单价',
  quantity INT NOT NULL COMMENT '数量',
  amount DECIMAL(12, 2) NOT NULL COMMENT '金额',
  remark TEXT COMMENT '备注',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (order_id) REFERENCES crm_sales_orders(id) ON DELETE CASCADE,
  FOREIGN KEY (product_id) REFERENCES crm_products(id),
  INDEX idx_order_id (order_id),
  INDEX idx_product_id (product_id)
);

