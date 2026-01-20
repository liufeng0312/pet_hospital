-- 添加 max_appointments 字段到 doctor_schedules 表
ALTER TABLE doctor_schedules 
ADD COLUMN max_appointments INT DEFAULT 10 COMMENT '最大预约数';

-- 更新现有记录的 max_appointments 值
-- 全天班默认15个，上午班和下午班默认8个，休息日为0
UPDATE doctor_schedules 
SET max_appointments = CASE 
    WHEN shift_type = 'FULL_DAY' THEN 15
    WHEN shift_type = 'MORNING' THEN 8
    WHEN shift_type = 'AFTERNOON' THEN 8
    WHEN shift_type = 'OFF' THEN 0
    ELSE 10
END;
