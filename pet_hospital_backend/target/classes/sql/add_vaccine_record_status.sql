-- 为疫苗记录表添加状态字段
ALTER TABLE vaccine_records ADD COLUMN `status` INT DEFAULT 0 COMMENT '状态: 0-未支付，1-待接种，2-已完成';

-- 为现有记录设置默认状态为待接种(1)
UPDATE vaccine_records SET status = 1 WHERE status IS NULL OR status = 0;
