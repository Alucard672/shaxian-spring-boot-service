-- ========== CRM用户表迁移脚本 ==========
-- 为crm_user_info表添加role_ids字段

ALTER TABLE crm_user_info 
ADD COLUMN role_ids TEXT NOT NULL COMMENT '角色ID列表，JSON数组格式，如[1,2,3]' AFTER email;
