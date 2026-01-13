-- ========== CRM角色表 ==========

-- CRM角色表（CRM系统角色，全局管理，不分租户）
CREATE TABLE crm_roles (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL COMMENT '角色名称',
  code VARCHAR(50) NOT NULL COMMENT '角色代码',
  level INT NOT NULL DEFAULT 0 COMMENT '角色级别，数值越大级别越高',
  status ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE' COMMENT '状态',
  description TEXT COMMENT '描述',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE INDEX ux_code (code),
  INDEX idx_status (status),
  INDEX idx_level (level)
);
