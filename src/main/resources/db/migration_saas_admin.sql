-- SaaS 平台后台 · 租户授权管理 数据库迁移
-- 关联 spec: specs/260512-saas-tenant-admin/
-- 适用：生产环境 (ddl-auto=none) 上线时一次性执行
-- dev/qiuxs 环境 (ddl-auto=update) 由 Hibernate 自动建表/加列，此脚本仅需用于种子数据 + 历史回填

-- ============================================================
-- 1. 新增"套餐"表（命名避开 Java 关键字 package）
-- ============================================================
CREATE TABLE IF NOT EXISTS packages (
    id               BIGINT          PRIMARY KEY AUTO_INCREMENT,
    name             VARCHAR(100)    NOT NULL COMMENT '套餐名',
    concurrent_limit INT             NOT NULL COMMENT '并发在线 session 上限',
    yearly_price     DECIMAL(10, 2)  NOT NULL COMMENT '年单价（元）',
    status           VARCHAR(16)     NOT NULL DEFAULT 'ACTIVE' COMMENT '状态 ACTIVE/INACTIVE',
    created_at       DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_packages_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='SaaS 套餐';

-- 种子：标准版
INSERT IGNORE INTO packages (name, concurrent_limit, yearly_price)
VALUES ('标准版', 3, 2000.00);

-- ============================================================
-- 2. 新增"订阅记录"表
-- ============================================================
CREATE TABLE IF NOT EXISTS subscriptions (
    id               BIGINT          PRIMARY KEY AUTO_INCREMENT,
    tenant_id        BIGINT          NOT NULL COMMENT '租户ID',
    amount           DECIMAL(10, 2)  NOT NULL DEFAULT 0 COMMENT '续费金额（0=赠送）',
    prev_expires_at  DATETIME        NULL     COMMENT '续费前的到期时间',
    new_expires_at   DATETIME        NOT NULL COMMENT '续费后的到期时间',
    operator_user_id BIGINT          NOT NULL COMMENT '操作人 user.id',
    note             VARCHAR(500)    NULL     COMMENT '备注',
    created_at       DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='租户订阅 / 续费记录';

CREATE INDEX idx_subscriptions_tenant   ON subscriptions(tenant_id);
CREATE INDEX idx_subscriptions_operator ON subscriptions(operator_user_id);

-- ============================================================
-- 3. tenants 表扩展字段
-- ============================================================
ALTER TABLE tenants ADD COLUMN package_id       BIGINT NULL COMMENT '套餐ID（关联 packages.id）';
ALTER TABLE tenants ADD COLUMN assigned_user_id BIGINT NULL COMMENT '业务员归属（关联 users.id，无 FK 约束）';

-- 历史数据回填：给所有未指定套餐的租户分配"标准版"
UPDATE tenants
   SET package_id = (SELECT id FROM packages WHERE name = '标准版')
 WHERE package_id IS NULL;
