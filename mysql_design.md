# 宠物医院管理系统 MySQL 数据库架构

以下是基于需求分析生成的 MySQL 建表语句 (DDL)。

## 1. 核心业务管理模块

### 1.1 客户管理

```sql
-- 客户表
CREATE TABLE `customers` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` VARCHAR(50) NOT NULL COMMENT '客户姓名',
  `phone` VARCHAR(20) NOT NULL COMMENT '联系电话',
  `address` VARCHAR(255) DEFAULT NULL COMMENT '地址',
  `level` TINYINT DEFAULT 2 COMMENT '客户等级 1:VIP, 2:普通, 3:黑名单',
  `notes` TEXT COMMENT '备注',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted_at` DATETIME DEFAULT NULL COMMENT '软删除时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户信息表';

-- 客户关系维护记录表
CREATE TABLE `customer_relations` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `customer_id` BIGINT NOT NULL COMMENT '客户ID',
  `type` VARCHAR(50) NOT NULL COMMENT '维护类型: 回访, 投诉, 生日祝福等',
  `content` TEXT COMMENT '沟通内容',
  `contact_time` DATETIME DEFAULT NULL COMMENT '联系时间',
  `staff_id` BIGINT DEFAULT NULL COMMENT '经办员工ID',
  PRIMARY KEY (`id`),
  KEY `idx_customer_id` (`customer_id`),
  KEY `idx_staff_id` (`staff_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户关系维护记录';
```

### 1.2 宠物档案管理

```sql
-- 宠物表
CREATE TABLE `pets` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `customer_id` BIGINT NOT NULL COMMENT '主人ID',
  `name` VARCHAR(50) NOT NULL COMMENT '宠物昵称',
  `species` VARCHAR(50) NOT NULL COMMENT '物种: 猫, 狗, 兔子等',
  `breed` VARCHAR(50) DEFAULT NULL COMMENT '品种',
  `gender` TINYINT DEFAULT 0 COMMENT '性别 1:公, 2:母, 0:未知',
  `birth_date` DATE DEFAULT NULL COMMENT '出生日期',
  `weight` DECIMAL(5,2) DEFAULT NULL COMMENT '体重(kg)',
  `photo_url` VARCHAR(255) DEFAULT NULL COMMENT '照片路径',
  `notes` TEXT COMMENT '特殊注意事项: 过敏史等',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted_at` DATETIME DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_customer_id` (`customer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宠物档案表';
```

### 1.3 诊疗管理

```sql
-- 挂号/预约记录表
CREATE TABLE `registrations` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `customer_id` BIGINT NOT NULL COMMENT '客户ID',
  `pet_id` BIGINT NOT NULL COMMENT '宠物ID',
  `doctor_id` BIGINT DEFAULT NULL COMMENT '医生ID',
  `status` VARCHAR(20) DEFAULT 'PENDING' COMMENT '状态: PENDING, CONFIRMED, COMPLETED, CANCELLED',
  `type` VARCHAR(20) DEFAULT 'APPOINTMENT' COMMENT '类型: APPOINTMENT(预约), ONSITE(现场挂号)',
  `appointment_time` DATETIME DEFAULT NULL COMMENT '预约时间',
  `queue_number` INT DEFAULT NULL COMMENT '排队号 (当天有效)',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_customer_pet` (`customer_id`, `pet_id`),
  KEY `idx_doctor_time` (`doctor_id`, `appointment_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='挂号预约记录表';

-- 病历表
CREATE TABLE `medical_records` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `registration_id` BIGINT NOT NULL COMMENT '挂号ID',
  `pet_id` BIGINT NOT NULL COMMENT '宠物ID',
  `doctor_id` BIGINT NOT NULL COMMENT '医生ID',
  `symptoms` TEXT COMMENT '主诉/症状',
  `diagnosis` TEXT COMMENT '诊断结果',
  `treatment_plan` TEXT COMMENT '治疗方案',
  `doctor_advice` TEXT COMMENT '医嘱',
  `visit_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '就诊时间',
  `deleted_at` DATETIME DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_pet_id` (`pet_id`),
  KEY `idx_registration_id` (`registration_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='病历表';

-- 处方表
CREATE TABLE `prescriptions` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `medical_record_id` BIGINT NOT NULL COMMENT '病历ID',
  `status` TINYINT DEFAULT 0 COMMENT '状态 0:未支付, 1:已支付, 2:已发药',
  `total_amount` DECIMAL(10,2) DEFAULT 0.00 COMMENT '总金额',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_medical_record_id` (`medical_record_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='处方表';

-- 处方详情表
CREATE TABLE `prescription_items` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `prescription_id` BIGINT NOT NULL COMMENT '处方ID',
  `drug_id` BIGINT NOT NULL COMMENT '药品ID',
  `quantity` INT NOT NULL DEFAULT 1 COMMENT '数量',
  `dosage` VARCHAR(100) DEFAULT NULL COMMENT '用法用量',
  `price` DECIMAL(10,2) NOT NULL COMMENT '单价 (快照)',
  PRIMARY KEY (`id`),
  KEY `idx_prescription_id` (`prescription_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='处方详情表';
```

## 2. 药品与库存管理

```sql
-- 药品信息表
CREATE TABLE `drugs` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` VARCHAR(100) NOT NULL COMMENT '药品名称',
  `code` VARCHAR(50) NOT NULL COMMENT '药品编码',
  `specification` VARCHAR(50) DEFAULT NULL COMMENT '规格',
  `manufacturer` VARCHAR(100) DEFAULT NULL COMMENT '厂家',
  `price` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '销售价格',
  `stock_quantity` INT NOT NULL DEFAULT 0 COMMENT '当前库存',
  `warning_threshold` INT DEFAULT 10 COMMENT '预警阈值',
  `unit` VARCHAR(20) DEFAULT NULL COMMENT '单位: 盒, 瓶, 支',
  `is_active` TINYINT DEFAULT 1 COMMENT '是否启用',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='药品信息库';

-- 库存变动记录表
CREATE TABLE `inventory_logs` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `drug_id` BIGINT NOT NULL COMMENT '药品ID',
  `type` TINYINT NOT NULL COMMENT '类型 1:入库, 2:出库, 3:盘点调整',
  `quantity` INT NOT NULL COMMENT '变动数量 (出库为负数)',
  `batch_number` VARCHAR(50) DEFAULT NULL COMMENT '批号',
  `expiry_date` DATE DEFAULT NULL COMMENT '有效期',
  `operator_id` BIGINT DEFAULT NULL COMMENT '操作人ID',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  PRIMARY KEY (`id`),
  KEY `idx_drug_id` (`drug_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存变动记录';
```

## 3. 医疗服务模块

```sql
-- 诊疗服务项目表
CREATE TABLE `services` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` VARCHAR(100) NOT NULL COMMENT '项目名称',
  `category` VARCHAR(50) NOT NULL COMMENT '类别: VACCINE, SURGERY, EXAM, LAB',
  `price` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '标准价格',
  `description` TEXT COMMENT '描述',
  `is_active` TINYINT DEFAULT 1 COMMENT '是否启用',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='诊疗服务项目';

-- 检验记录表
CREATE TABLE `lab_tests` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `medical_record_id` BIGINT NOT NULL COMMENT '病历ID',
  `service_id` BIGINT NOT NULL COMMENT '检验项目ID',
  `result` TEXT COMMENT '检验结果 (JSON或文本)',
  `report_url` VARCHAR(255) DEFAULT NULL COMMENT '报告文件路径',
  `test_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '检验时间',
  PRIMARY KEY (`id`),
  KEY `idx_medical_record` (`medical_record_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='检验记录';

-- 住院记录表
CREATE TABLE `hospitalizations` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `pet_id` BIGINT NOT NULL COMMENT '宠物ID',
  `bed_number` VARCHAR(20) DEFAULT NULL COMMENT '床位号',
  `start_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '入院时间',
  `end_time` DATETIME DEFAULT NULL COMMENT '出院时间',
  `status` VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE, DISCHARGED',
  PRIMARY KEY (`id`),
  KEY `idx_pet_status` (`pet_id`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='住院记录';

-- 护理记录表
CREATE TABLE `care_records` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `hospitalization_id` BIGINT NOT NULL COMMENT '住院ID',
  `content` TEXT COMMENT '护理内容',
  `operator_id` BIGINT DEFAULT NULL COMMENT '操作人ID',
  `record_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '记录时间',
  PRIMARY KEY (`id`),
  KEY `idx_hospitalization` (`hospitalization_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='住院护理记录';
```

## 4. 运营与财务模块

```sql
-- 收费单表
CREATE TABLE `bills` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `customer_id` BIGINT NOT NULL COMMENT '客户ID',
  `related_type` VARCHAR(50) COMMENT '关联类型: REGISTRATION, PRESCRIPTION',
  `related_id` BIGINT COMMENT '关联业务ID',
  `total_amount` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '应收金额',
  `discount_amount` DECIMAL(10,2) DEFAULT 0.00 COMMENT '优惠金额',
  `final_amount` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '实收金额',
  `payment_method` VARCHAR(20) DEFAULT NULL COMMENT '支付方式: CASH, WECHAT, ALIPAY, CARD',
  `status` TINYINT DEFAULT 0 COMMENT '状态 0:未付, 1:已付, 2:退款, 3:部分支付',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `paid_at` DATETIME DEFAULT NULL COMMENT '支付时间',
  PRIMARY KEY (`id`),
  KEY `idx_customer_id` (`customer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收费单';

-- 财务统计表 (每日/每月)
CREATE TABLE `financial_stats` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `stat_date` DATE NOT NULL COMMENT '统计日期',
  `total_income` DECIMAL(12,2) DEFAULT 0.00 COMMENT '总收入',
  `total_expense` DECIMAL(12,2) DEFAULT 0.00 COMMENT '总支出',
  `category` VARCHAR(50) DEFAULT 'ALL' COMMENT '类别: ALL, DRUG, SERVICE',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_date_category` (`stat_date`, `category`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='财务统计';
```

## 5. 系统管理模块

```sql
-- 员工表
CREATE TABLE `employees` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` VARCHAR(50) NOT NULL COMMENT '姓名',
  `username` VARCHAR(50) NOT NULL COMMENT '登录账号',
  `password_hash` VARCHAR(255) NOT NULL COMMENT '密码哈希',
  `role` VARCHAR(50) NOT NULL COMMENT '角色: DOCTOR, RECEPTIONIST, ADMIN',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
  `status` TINYINT DEFAULT 1 COMMENT '状态 1:在职, 0:离职',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工表';

-- 系统配置表
CREATE TABLE `system_settings` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `key_name` VARCHAR(100) NOT NULL COMMENT '配置键',
  `value` TEXT COMMENT '配置值',
  `report_url` VARCHAR(255) DEFAULT NULL COMMENT '报告文件路径',
  `test_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '检验时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_key_name` (`key_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置';
```

## 6. 客服与提醒模块

```sql
-- 提醒记录表
CREATE TABLE `reminders` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `customer_id` BIGINT NOT NULL COMMENT '客户ID',
  `pet_id` BIGINT DEFAULT NULL COMMENT '宠物ID',
  `type` VARCHAR(50) NOT NULL COMMENT '提醒类型: VACCINE, FOLLOW_UP, BIRTHDAY',
  `due_date` DATE NOT NULL COMMENT '到期日期',
  `status` TINYINT DEFAULT 0 COMMENT '状态 0:未发送, 1:已发送, 2:发送失败',
  `sent_time` DATETIME DEFAULT NULL COMMENT '发送时间',
  PRIMARY KEY (`id`),
  KEY `idx_due_date` (`due_date`),
  KEY `idx_customer` (`customer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='提醒服务记录';
```
