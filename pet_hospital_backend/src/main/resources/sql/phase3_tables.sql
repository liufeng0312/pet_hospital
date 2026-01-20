-- Phase 3 数据库表结构
-- 在线咨询、健康资讯、消息通知

-- 1. 在线咨询表
CREATE TABLE IF NOT EXISTS `consultations` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `customer_id` BIGINT NOT NULL COMMENT '客户ID',
  `pet_id` BIGINT DEFAULT NULL COMMENT '关联宠物ID (可选)',
  `title` VARCHAR(200) NOT NULL COMMENT '咨询标题',
  `content` TEXT NOT NULL COMMENT '咨询内容',
  `images` TEXT COMMENT '图片URL列表(JSON数组)',
  `status` VARCHAR(20) DEFAULT 'PENDING' COMMENT '状态: PENDING(待回复), ANSWERED(已回复), CLOSED(已关闭)',
  `reply_content` TEXT COMMENT '兽医回复内容',
  `reply_doctor_id` BIGINT DEFAULT NULL COMMENT '回复医生ID',
  `reply_time` DATETIME DEFAULT NULL COMMENT '回复时间',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted_at` DATETIME DEFAULT NULL COMMENT '软删除时间',
  PRIMARY KEY (`id`),
  KEY `idx_customer_id` (`customer_id`),
  KEY `idx_pet_id` (`pet_id`),
  KEY `idx_status` (`status`),
  KEY `idx_reply_doctor_id` (`reply_doctor_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='在线咨询表';

-- 2. 健康资讯文章表
CREATE TABLE IF NOT EXISTS `articles` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `title` VARCHAR(200) NOT NULL COMMENT '文章标题',
  `category` VARCHAR(50) DEFAULT NULL COMMENT '分类: 疾病预防, 日常护理, 饮食营养, 行为训练等',
  `cover_image` VARCHAR(255) DEFAULT NULL COMMENT '封面图片URL',
  `summary` VARCHAR(500) DEFAULT NULL COMMENT '摘要',
  `content` TEXT NOT NULL COMMENT '文章内容(富文本)',
  `author` VARCHAR(100) DEFAULT NULL COMMENT '作者',
  `view_count` INT DEFAULT 0 COMMENT '浏览次数',
  `is_published` TINYINT DEFAULT 1 COMMENT '是否发布: 1=已发布, 0=草稿',
  `published_at` DATETIME DEFAULT NULL COMMENT '发布时间',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted_at` DATETIME DEFAULT NULL COMMENT '软删除时间',
  PRIMARY KEY (`id`),
  KEY `idx_category` (`category`),
  KEY `idx_published` (`is_published`, `published_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='健康资讯文章表';

-- 3. 消息通知表
CREATE TABLE IF NOT EXISTS `notifications` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `customer_id` BIGINT NOT NULL COMMENT '客户ID',
  `type` VARCHAR(50) NOT NULL COMMENT '通知类型: APPOINTMENT, REMINDER, CONSULTATION, SYSTEM',
  `title` VARCHAR(200) NOT NULL COMMENT '通知标题',
  `content` TEXT NOT NULL COMMENT '通知内容',
  `related_type` VARCHAR(50) DEFAULT NULL COMMENT '关联类型: appointment, reminder, consultation等',
  `related_id` BIGINT DEFAULT NULL COMMENT '关联ID',
  `is_read` TINYINT DEFAULT 0 COMMENT '是否已读: 1=已读, 0=未读',
  `read_at` DATETIME DEFAULT NULL COMMENT '阅读时间',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_customer_read` (`customer_id`, `is_read`),
  KEY `idx_type` (`type`),
  KEY `idx_related` (`related_type`, `related_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息通知表';
