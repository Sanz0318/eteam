-- ============================================================
-- 统一权限管理系统 · RBAC0 初始化数据
-- 前置条件：已执行 01_schema.sql
-- 说明：使用 INSERT IGNORE，可重复执行；已存在的记录不会被改动
-- ============================================================

USE `rbac`;

-- ------------------------------------------------------------
-- 1. 内置超级管理员角色（level=0，数值越小等级越高）
-- ------------------------------------------------------------
INSERT IGNORE INTO `sys_role` (`role_name`, `role_code`, `level`, `description`) VALUES
  ('超级管理员', 'SUPER_ADMIN', 0, '系统内置最高管理员');

-- ------------------------------------------------------------
-- 2. 示例权限（= 资源 + 操作）
-- ------------------------------------------------------------
INSERT IGNORE INTO `sys_permission` (`permission_name`, `resource`, `operation`, `permission_code`, `description`) VALUES
  ('创建用户',     'user', 'create', 'user:create', '创建用户'),
  ('查看用户',     'user', 'read',   'user:read',   '查看用户'),
  ('修改用户',     'user', 'update', 'user:update', '修改用户'),
  ('删除用户',     'user', 'delete', 'user:delete', '删除用户'),
  ('创建角色',     'role', 'create', 'role:create', '创建角色'),
  ('分配角色权限', 'role', 'assign', 'role:assign', '给角色分配权限');

-- ------------------------------------------------------------
-- 3. 将示例权限授予超级管理员（PA）
--    通过编码关联，避免写死自增主键
-- ------------------------------------------------------------
INSERT IGNORE INTO `sys_role_permission` (`role_id`, `permission_id`)
SELECT r.`id`, p.`id`
FROM `sys_role` r
JOIN `sys_permission` p
  ON p.`permission_code` IN (
    'user:create', 'user:read', 'user:update', 'user:delete',
    'role:create', 'role:assign'
  )
WHERE r.`role_code` = 'SUPER_ADMIN';

-- ------------------------------------------------------------
-- 4. 管理员账号说明
--    admin / 123456 由后端 DataInitializer 在应用首次启动时创建，
--    密码使用 BCrypt 动态加密；此处不写死哈希，避免与后端实现不一致。
--    若仅执行 SQL 而不启动应用，则暂无登录账号，属正常现象。
-- ------------------------------------------------------------
