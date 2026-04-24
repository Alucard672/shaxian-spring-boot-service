-- 让销售明细的色号字段可空，以兼容 SaaS 产品授权场景（无色号概念）
-- 纱线业务继续按以往传色号，不受影响

ALTER TABLE sales_order_items MODIFY COLUMN color_id BIGINT NULL;
ALTER TABLE sales_order_items MODIFY COLUMN color_name VARCHAR(100) NULL;
ALTER TABLE sales_order_items MODIFY COLUMN color_code VARCHAR(50) NULL;
