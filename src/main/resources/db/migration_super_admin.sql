-- 给 users 表新增 is_super_admin 字段（生产环境 ddl-auto=none，需手动执行一次）
-- 测试 / dev 环境 ddl-auto=update，Hibernate 会自动加列，不需要执行此脚本

-- MySQL 8.0.29+ 支持 IF NOT EXISTS；低版本请删除 IF NOT EXISTS 后执行
ALTER TABLE users
    ADD COLUMN IF NOT EXISTS is_super_admin BOOLEAN NOT NULL DEFAULT FALSE;

-- 初始化平台超级管理员（按需修改手机号）
INSERT IGNORE INTO users (phone, name, password, status, is_super_admin, created_at, updated_at) VALUES (
    '13003629527',
    '平台超级管理员',
    'Admin@123456',
    'ACTIVE',
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

UPDATE users SET is_super_admin = TRUE WHERE phone = '13003629527';
