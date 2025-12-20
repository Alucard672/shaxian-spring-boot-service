-- 租户创建功能数据库迁移脚本
-- 支持 MySQL 5.7+ / PostgreSQL 10+

-- ========== 修改 tenants 表 ==========

-- 添加 address 字段（租户地址，先允许 NULL）
ALTER TABLE tenants ADD COLUMN address TEXT AFTER code;

-- 为已存在的租户设置默认地址
UPDATE tenants SET address = '' WHERE address IS NULL;

-- 设置 address 字段为 NOT NULL
ALTER TABLE tenants MODIFY COLUMN address TEXT NOT NULL;

-- 添加 expires_at 字段（有效期截止时间）
ALTER TABLE tenants ADD COLUMN expires_at DATETIME AFTER address;

-- ========== 修改 user_tenants 表 ==========

-- 添加 relationship_type 字段（用户与租户的关系类型）
ALTER TABLE user_tenants ADD COLUMN relationship_type ENUM('OWNER', 'MEMBER') NOT NULL DEFAULT 'OWNER' AFTER is_default;

-- 为 relationship_type 添加索引
ALTER TABLE user_tenants ADD INDEX idx_relationship_type (relationship_type);

-- ========== 数据迁移 ==========

-- 为已存在的 user_tenants 记录设置默认关系类型（如果为空）
-- 注意：由于 relationship_type 有 DEFAULT 'OWNER'，已存在的记录应该已经有默认值
-- 但如果需要确保，可以执行以下语句：
-- UPDATE user_tenants SET relationship_type = 'OWNER' WHERE relationship_type IS NULL;

