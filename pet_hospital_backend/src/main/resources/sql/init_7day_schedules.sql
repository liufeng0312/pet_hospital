-- 生成未来7天的排班数据
-- 为所有在职医生创建排班，采用轮班制度

-- 清空现有排班数据
TRUNCATE TABLE doctor_schedules;

-- 插入未来7天的排班
-- 医生ID: 1-张医生, 2-李医生, 3-王医生, 4-赵医生
-- 班次: MORNING(上午), AFTERNOON(下午), FULL_DAY(全天), OFF(休息)

-- 第1天 (今天)
INSERT INTO doctor_schedules (doctor_id, work_date, shift_type, is_available) VALUES
(1, CURDATE(), 'FULL_DAY', 1),
(2, CURDATE(), 'FULL_DAY', 1),
(3, CURDATE(), 'MORNING', 1),
(4, CURDATE(), 'AFTERNOON', 1);

-- 第2天
INSERT INTO doctor_schedules (doctor_id, work_date, shift_type, is_available) VALUES
(1, DATE_ADD(CURDATE(), INTERVAL 1 DAY), 'FULL_DAY', 1),
(2, DATE_ADD(CURDATE(), INTERVAL 1 DAY), 'MORNING', 1),
(3, DATE_ADD(CURDATE(), INTERVAL 1 DAY), 'FULL_DAY', 1),
(4, DATE_ADD(CURDATE(), INTERVAL 1 DAY), 'AFTERNOON', 1);

-- 第3天
INSERT INTO doctor_schedules (doctor_id, work_date, shift_type, is_available) VALUES
(1, DATE_ADD(CURDATE(), INTERVAL 2 DAY), 'MORNING', 1),
(2, DATE_ADD(CURDATE(), INTERVAL 2 DAY), 'FULL_DAY', 1),
(3, DATE_ADD(CURDATE(), INTERVAL 2 DAY), 'AFTERNOON', 1),
(4, DATE_ADD(CURDATE(), INTERVAL 2 DAY), 'FULL_DAY', 1);

-- 第4天
INSERT INTO doctor_schedules (doctor_id, work_date, shift_type, is_available) VALUES
(1, DATE_ADD(CURDATE(), INTERVAL 3 DAY), 'AFTERNOON', 1),
(2, DATE_ADD(CURDATE(), INTERVAL 3 DAY), 'MORNING', 1),
(3, DATE_ADD(CURDATE(), INTERVAL 3 DAY), 'FULL_DAY', 1),
(4, DATE_ADD(CURDATE(), INTERVAL 3 DAY), 'OFF', 0);

-- 第5天
INSERT INTO doctor_schedules (doctor_id, work_date, shift_type, is_available) VALUES
(1, DATE_ADD(CURDATE(), INTERVAL 4 DAY), 'FULL_DAY', 1),
(2, DATE_ADD(CURDATE(), INTERVAL 4 DAY), 'OFF', 0),
(3, DATE_ADD(CURDATE(), INTERVAL 4 DAY), 'FULL_DAY', 1),
(4, DATE_ADD(CURDATE(), INTERVAL 4 DAY), 'MORNING', 1);

-- 第6天
INSERT INTO doctor_schedules (doctor_id, work_date, shift_type, is_available) VALUES
(1, DATE_ADD(CURDATE(), INTERVAL 5 DAY), 'MORNING', 1),
(2, DATE_ADD(CURDATE(), INTERVAL 5 DAY), 'FULL_DAY', 1),
(3, DATE_ADD(CURDATE(), INTERVAL 5 DAY), 'OFF', 0),
(4, DATE_ADD(CURDATE(), INTERVAL 5 DAY), 'FULL_DAY', 1);

-- 第7天
INSERT INTO doctor_schedules (doctor_id, work_date, shift_type, is_available) VALUES
(1, DATE_ADD(CURDATE(), INTERVAL 6 DAY), 'FULL_DAY', 1),
(2, DATE_ADD(CURDATE(), INTERVAL 6 DAY), 'AFTERNOON', 1),
(3, DATE_ADD(CURDATE(), INTERVAL 6 DAY), 'FULL_DAY', 1),
(4, DATE_ADD(CURDATE(), INTERVAL 6 DAY), 'MORNING', 1);

-- 验证插入结果
SELECT 
    ds.id,
    e.name AS doctor_name,
    ds.work_date,
    ds.shift_type,
    ds.is_available
FROM doctor_schedules ds
JOIN employees e ON ds.doctor_id = e.id
ORDER BY ds.work_date, ds.doctor_id;
