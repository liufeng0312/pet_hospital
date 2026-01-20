-- 手术记录表(无外键约束)
CREATE TABLE IF NOT EXISTS surgery_records (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '手术记录ID',
    service_item_id BIGINT NOT NULL COMMENT '服务项目ID',
    medical_record_id BIGINT NOT NULL COMMENT '病历ID',
    pet_id BIGINT NOT NULL COMMENT '宠物ID',
    surgeon_id BIGINT NOT NULL COMMENT '主刀医生ID',
    assistant_ids VARCHAR(255) COMMENT '助手医生ID列表（逗号分隔）',
    
    surgery_type VARCHAR(100) NOT NULL COMMENT '手术类型',
    surgery_name VARCHAR(200) NOT NULL COMMENT '手术名称',
    surgery_date DATETIME NOT NULL COMMENT '手术日期',
    duration INT COMMENT '手术时长（分钟）',
    
    pre_diagnosis TEXT COMMENT '术前诊断',
    surgery_process TEXT COMMENT '手术过程',
    post_diagnosis TEXT COMMENT '术后诊断',
    anesthesia_type VARCHAR(100) COMMENT '麻醉方式',
    complications TEXT COMMENT '并发症',
    notes TEXT COMMENT '备注',
    
    status TINYINT DEFAULT 0 COMMENT '状态：0-待支付，1-已支付待手术，2-手术中，3-已完成',
    amount DECIMAL(10,2) NOT NULL COMMENT '手术费用',
    
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at DATETIME DEFAULT NULL,
    
    INDEX idx_pet_id (pet_id),
    INDEX idx_medical_record_id (medical_record_id),
    INDEX idx_surgeon_id (surgeon_id),
    INDEX idx_surgery_date (surgery_date),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='手术记录表';
