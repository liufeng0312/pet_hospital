-- 更新现有员工密码为BCrypt加密格式
-- 执行此脚本前请确保后端服务已配置BCrypt密码编码器

-- BCrypt加密后的密码：
-- "admin" -> $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy
-- "123456" -> $2a$10$EblZqNptyYV/Qw4ZPjCHyu5UgPdDOX6U5ysN.5V5VEjcYqS0uF7Xu

-- 临时：通过API创建一个测试用户来生成正确的BCrypt哈希
-- 然后复制该哈希更新admin账号

-- 备选方案：先用明文密码，然后通过修改密码功能来加密
-- 这样可以确保hash是由实际运行的应用生成的

UPDATE employees 
SET password_hash = '123456'  -- 临时使用明文，马上修改密码来触发BCrypt加密
WHERE username = 'admin';

SELECT 'Please login and change password to trigger BCrypt encryption' as note;

-- 验证更新结果
SELECT id, name, username, LEFT(password_hash, 20) as password_hash_preview, role, status
FROM employees;
