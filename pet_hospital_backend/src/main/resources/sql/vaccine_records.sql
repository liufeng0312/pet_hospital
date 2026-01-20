-- 疫苗接种记录表
CREATE TABLE IF NOT EXISTS `vaccine_records` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `pet_id` BIGINT NOT NULL COMMENT '宠物ID',
  `vaccine_name` VARCHAR(100) NOT NULL COMMENT '疫苗名称',
  `vaccine_type` VARCHAR(50) DEFAULT NULL COMMENT '疫苗类型: 狂犬/六联/三联/其他',
  `batch_number` VARCHAR(100) DEFAULT NULL COMMENT '批号',
  `vaccination_date` DATE NOT NULL COMMENT '接种日期',
  `next_due_date` DATE DEFAULT NULL COMMENT '下次接种日期',
  `doctor_id` BIGINT DEFAULT NULL COMMENT '接种医生ID',
  `notes` TEXT COMMENT '备注',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted_at` DATETIME DEFAULT NULL COMMENT '软删除时间',
  PRIMARY KEY (`id`),
  KEY `idx_pet_id` (`pet_id`),
  KEY `idx_vaccination_date` (`vaccination_date`),
  KEY `idx_next_due_date` (`next_due_date`),
  KEY `idx_doctor_id` (`doctor_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='疫苗接种记录表';
