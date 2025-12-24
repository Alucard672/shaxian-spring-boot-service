-- ========== CRM客户表 ==========

-- CRM客户表（软件销售客户，全局管理，不分租户）
CREATE TABLE crm_customers (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(200) NOT NULL,
  address TEXT,
  phone VARCHAR(50) NOT NULL,
  remark TEXT,
  source ENUM('ONLINE', 'OFFLINE', 'REFERRAL', 'EXHIBITION', 'ADVERTISING', 'OTHER') NOT NULL,
  type ENUM('OFFICIAL', 'POTENTIAL') NOT NULL DEFAULT 'POTENTIAL',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE INDEX ux_phone (phone),
  INDEX idx_type (type)
);

-- CRM商品表（软件产品，全局管理，不分租户）
CREATE TABLE crm_products (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(200) NOT NULL,
  code VARCHAR(50) NOT NULL,
  unit_price DECIMAL(12, 2) NOT NULL COMMENT '单价',
  discount_price DECIMAL(12, 2) COMMENT '优惠价',
  product_value DECIMAL(12, 2) COMMENT '商品价值',
  license_count INT COMMENT '授权数（用于定价）',
  status ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE',
  description TEXT,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE INDEX ux_code (code),
  INDEX idx_status (status),
  INDEX idx_license_count (license_count)
);
