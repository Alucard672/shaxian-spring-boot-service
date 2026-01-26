-- 短码功能数据库迁移脚本
-- 支持 MySQL 5.7+ / PostgreSQL 10+

-- ========== 短码表 ==========
-- 用于存储短码与原始分享码的映射关系
-- 注意：此表不区分租户，所有租户的短码数据统一存储

CREATE TABLE short_codes (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    short_code VARCHAR(6) NOT NULL UNIQUE,
    original_code TEXT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_short_code (short_code)
);
