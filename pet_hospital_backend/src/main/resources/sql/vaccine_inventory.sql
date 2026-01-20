-- 疫苗库存管理表
CREATE TABLE IF NOT EXISTS `vaccine_inventory` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `vaccine_name` VARCHAR(100) NOT NULL COMMENT '疫苗名称',
  `vaccine_type` VARCHAR(50) DEFAULT NULL COMMENT '疫苗类型',
  `manufacturer` VARCHAR(100) DEFAULT NULL COMMENT '生产厂家',
  `batch_number` VARCHAR(100) NOT NULL COMMENT '批号',
  `quantity` INT NOT NULL DEFAULT 0 COMMENT '库存数量',
  `min_quantity` INT DEFAULT 10 COMMENT '最低库存预警值',
  `unit_price` DECIMAL(10,2) DEFAULT NULL COMMENT '单价',
  `production_date` DATE DEFAULT NULL COMMENT '生产日期',
  `expiry_date` DATE DEFAULT NULL COMMENT '有效期至',
  `storage_location` VARCHAR(100) DEFAULT NULL COMMENT '存储位置',
  `notes` TEXT COMMENT '备注',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted_at` DATETIME DEFAULT NULL COMMENT '软删除时间',
  PRIMARY KEY (`id`),
  KEY `idx_vaccine_name` (`vaccine_name`),
  KEY `idx_batch_number` (`batch_number`),
  KEY `idx_expiry_date` (`expiry_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='疫苗库存表';

-- 库存变动日志表
CREATE TABLE IF NOT EXISTS `vaccine_stock_logs` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `inventory_id` BIGINT NOT NULL COMMENT '库存ID',
  `change_type` VARCHAR(20) NOT NULL COMMENT '变动类型: IN(入库), OUT(出库), ADJUST(调整)',
  `quantity` INT NOT NULL COMMENT '变动数量',
  `related_record_id` BIGINT DEFAULT NULL COMMENT '关联疫苗接种记录ID',
  `operator_id` BIGINT DEFAULT NULL COMMENT '操作员工ID',
  `reason` VARCHAR(200) DEFAULT NULL COMMENT '变动原因',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_inventory_id` (`inventory_id`),
  KEY `idx_change_type` (`change_type`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='疫苗库存变动日志表';
