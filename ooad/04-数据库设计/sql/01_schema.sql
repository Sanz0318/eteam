-- ============================================================
-- 统一权限管理系统 · RBAC0 建表脚本
-- 目标数据库：MySQL 8.x，字符集 utf8mb4
-- 对应文档：ooad/04-数据库设计/数据库设计.md
-- 说明：脚本可重复执行（IF NOT EXISTS），不会删除或覆盖已有数据
-- ============================================================

CREATE DATABASE IF NOT EXISTS `rbac`
  DEFAULT CHARACTER SET utf8mb4;

USE `rbac`;

-- ------------------------------------------------------------
-- 1. 用户表
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `sys_user` (
  `id`         BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username`   VARCHAR(64)  NOT NULL                COMMENT '登录账号',
  `password`   VARCHAR(128) NOT NULL                COMMENT '密码(BCrypt 加密存储)',
  `nickname`   VARCHAR(64)  DEFAULT NULL            COMMENT '昵称',
  `status`     TINYINT      NOT NULL DEFAULT 1      COMMENT '状态 1启用 0停用',
  `created_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ------------------------------------------------------------
-- 2. 角色表（level 为可自定义的管理员等级）
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `sys_role` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `role_name`   VARCHAR(64)  NOT NULL                COMMENT '角色名称',
  `role_code`   VARCHAR(64)  NOT NULL                COMMENT '角色编码',
  `level`       INT          NOT NULL DEFAULT 0      COMMENT '管理员等级(可自定义,数值越小等级越高)',
  `description` VARCHAR(255) DEFAULT NULL            COMMENT '描述',
  `status`      TINYINT      NOT NULL DEFAULT 1      COMMENT '状态 1启用 0停用',
  `created_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_code` (`role_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- ------------------------------------------------------------
-- 3. 权限表（权限 = 资源 + 操作，编码形如 order:delete）
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `sys_permission` (
  `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `permission_name` VARCHAR(64)  NOT NULL                COMMENT '权限名称',
  `resource`        VARCHAR(64)  NOT NULL                COMMENT '资源',
  `operation`       VARCHAR(64)  NOT NULL                COMMENT '操作',
  `permission_code` VARCHAR(128) NOT NULL                COMMENT '权限编码 如 order:delete',
  `description`     VARCHAR(255) DEFAULT NULL            COMMENT '描述',
  `created_at`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_permission_code` (`permission_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

-- ------------------------------------------------------------
-- 4. 用户-角色关联表（UA，多对多）
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `sys_user_role` (
  `id`      BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `role_id` BIGINT NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_role` (`user_id`, `role_id`),
  KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户-角色关联表';

-- ------------------------------------------------------------
-- 5. 角色-权限关联表（PA，多对多）
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `sys_role_permission` (
  `id`            BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `role_id`       BIGINT NOT NULL COMMENT '角色ID',
  `permission_id` BIGINT NOT NULL COMMENT '权限ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_perm` (`role_id`, `permission_id`),
  KEY `idx_permission_id` (`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色-权限关联表';
