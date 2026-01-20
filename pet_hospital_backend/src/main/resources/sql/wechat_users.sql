-- 创建微信用户表
CREATE TABLE IF NOT EXISTS `wechat_users` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `openid` VARCHAR(100) NOT NULL COMMENT '微信openid',
  `unionid` VARCHAR(100) COMMENT '微信unionid',
  `customer_id` BIGINT COMMENT '关联客户ID',
  `nickname` VARCHAR(100) COMMENT '微信昵称',
  `avatar_url` VARCHAR(255) COMMENT '微信头像URL',
  `phone` VARCHAR(20) COMMENT '绑定手机号',
  `session_key` VARCHAR(100) COMMENT '微信session_key',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_openid` (`openid`),
  KEY `idx_customer_id` (`customer_id`),
  KEY `idx_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='微信用户表';

-- 添加测试数据
INSERT INTO `wechat_users` (`openid`, `customer_id`, `nickname`, `phone`) VALUES
('test_openid_001', 1, '测试用户1', '13800138000'),
('test_openid_002', 2, '测试用户2', '13800138001');
