# 统一权限管理系统 — 建表 SQL 说明

> 配套数据库：MySQL 8.x（InnoDB，utf8mb4）
> 设计依据：《统一权限管理系统 — 数据库设计》（`数据库设计.md`）
> 交付范围：RBAC0 核心 5 张表（用户、角色、权限、UA、PA）

---

## 1. SQL 文件清单

| 文件 | 作用 | 是否可重复执行 |
|------|------|----------------|
| `sql/01_schema.sql` | 创建数据库 `rbac` 与 5 张核心表 | 是（`IF NOT EXISTS`，不删除已有数据） |
| `sql/02_init_data.sql` | 写入超级管理员角色、6 条示例权限及其 PA 关联 | 是（`INSERT IGNORE`，不覆盖已有记录） |

**执行顺序**：先 `01_schema.sql`，再 `02_init_data.sql`。

---

## 2. 执行方式

### 2.1 命令行（MySQL 客户端）

```bash
mysql -uroot -p < ooad/04-数据库设计/sql/01_schema.sql
mysql -uroot -p < ooad/04-数据库设计/sql/02_init_data.sql
```

脚本内已包含 `USE rbac;`，无需手动指定数据库。

### 2.2 Docker（docker-compose 启动的 MySQL）

```bash
docker exec -i rbac-mysql mysql -uroot -p123456 < ooad/04-数据库设计/sql/01_schema.sql
docker exec -i rbac-mysql mysql -uroot -p123456 < ooad/04-数据库设计/sql/02_init_data.sql
```

### 2.3 图形化工具（Navicat / DataGrip 等）

打开 `01_schema.sql` 与 `02_init_data.sql`，按顺序整体执行即可。

> 也可以不执行 SQL：后端 `application.yml` 配置了 `ddl-auto: update`，首次启动会自动建表；
> `DataInitializer` 会自动写入示例权限、超级管理员角色，并创建 `admin / 123456` 账号。
> 手工执行本 SQL 适合“先建库、再启动应用”或需要独立交付数据库脚本的场景。

---

## 3. 表结构说明

ER 关系：`sys_user ←UA→ sys_role ←PA→ sys_permission`，两张关联表分别表达 UA 与 PA 多对多关系。用户不直接持有权限，只能经由角色间接获得。

| 表名 | 作用 | 对应 RBAC0 概念 | 关键约束 |
|------|------|-----------------|----------|
| `sys_user` | 用户账号 | User | `username` 唯一 |
| `sys_role` | 角色，含可自定义管理员等级 `level` | Role | `role_code` 唯一 |
| `sys_permission` | 权限，由“资源 + 操作”两段式表达 | Permission | `permission_code` 唯一 |
| `sys_user_role` | 用户-角色分配 | UA | `(user_id, role_id)` 唯一 |
| `sys_role_permission` | 角色-权限分配 | PA | `(role_id, permission_id)` 唯一 |

### 3.1 sys_user（用户表）

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | BIGINT | PK，自增 | 主键 |
| username | VARCHAR(64) | NOT NULL，UK | 登录账号，全局唯一 |
| password | VARCHAR(128) | NOT NULL | BCrypt 加密后的密码，禁止明文 |
| nickname | VARCHAR(64) | NULL | 昵称 |
| status | TINYINT | NOT NULL，默认 1 | 1 启用 / 0 停用；停用后鉴权立即拒绝 |
| created_at | DATETIME | NOT NULL，默认当前时间 | 创建时间 |
| updated_at | DATETIME | NOT NULL，自动更新 | 更新时间 |

### 3.2 sys_role（角色表）

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | BIGINT | PK，自增 | 主键 |
| role_name | VARCHAR(64) | NOT NULL | 角色名称 |
| role_code | VARCHAR(64) | NOT NULL，UK | 角色编码，全局唯一 |
| level | INT | NOT NULL，默认 0 | 管理员等级，可自定义，数值越小等级越高 |
| description | VARCHAR(255) | NULL | 描述 |
| status | TINYINT | NOT NULL，默认 1 | 1 启用 / 0 停用 |
| created_at | DATETIME | NOT NULL，默认当前时间 | 创建时间 |
| updated_at | DATETIME | NOT NULL，自动更新 | 更新时间 |

### 3.3 sys_permission（权限表）

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | BIGINT | PK，自增 | 主键 |
| permission_name | VARCHAR(64) | NOT NULL | 权限名称 |
| resource | VARCHAR(64) | NOT NULL | 资源，如 `order` |
| operation | VARCHAR(64) | NOT NULL | 操作，如 `delete` |
| permission_code | VARCHAR(128) | NOT NULL，UK | 权限编码，如 `order:delete`，全局唯一 |
| description | VARCHAR(255) | NULL | 描述 |
| created_at | DATETIME | NOT NULL，默认当前时间 | 创建时间 |

> 权限不设 `status` 字段：第一轮通过“删除权限”或“从角色中移除”使其失效，角色/用户停用则另行控制。

### 3.4 sys_user_role（用户-角色关联表）

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | BIGINT | PK，自增 | 主键 |
| user_id | BIGINT | NOT NULL | 用户 ID |
| role_id | BIGINT | NOT NULL | 角色 ID |

### 3.5 sys_role_permission（角色-权限关联表）

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | BIGINT | PK，自增 | 主键 |
| role_id | BIGINT | NOT NULL | 角色 ID |
| permission_id | BIGINT | NOT NULL | 权限 ID |

---

## 4. 约束与索引设计

| 索引 / 约束 | 所属表 | 设计意图 |
|-------------|--------|----------|
| `uk_username` | sys_user | 保证登录账号唯一，同时支撑按用户名登录查询 |
| `uk_role_code` | sys_role | 保证角色编码唯一 |
| `uk_permission_code` | sys_permission | 保证权限编码唯一；鉴权按编码精确匹配 |
| `uk_user_role (user_id, role_id)` | sys_user_role | 防止重复分配；最左前缀 `user_id` 支撑“查用户拥有的角色” |
| `idx_role_id` | sys_user_role | 支撑反向查询“拥有某角色的用户” |
| `uk_role_perm (role_id, permission_id)` | sys_role_permission | 防止重复授权；最左前缀 `role_id` 支撑“查角色拥有的权限” |
| `idx_permission_id` | sys_role_permission | 支撑反向查询“哪些角色拥有该权限” |

鉴权查询路径与索引的对应关系（对应 `UserRepository.countPermission`）：

```
sys_user_role.user_id →(uk_user_role 最左前缀)→ sys_role_permission.role_id
→(uk_role_perm 最左前缀)→ sys_permission.permission_code (uk_permission_code)
```

整条链路均走索引，避免全表扫描；第一轮按单条联表 COUNT 查询实现。

**关于外键**：本设计未添加数据库外键约束，与 JPA `@ManyToMany` 的关联表策略保持一致，关联完整性由 Service 层事务保障。若后续需要数据库级强约束，可在两张关联表上补充 `FOREIGN KEY ... ON DELETE CASCADE`。

**关于会话（Session）**：第一轮会话使用 JWT 无状态实现，token 即会话凭证，不落库，因此没有 `sys_session` 表。若后续需要“踢人下线/会话管理”，再单独增加该表。

---

## 5. 初始化数据说明

`02_init_data.sql` 写入以下数据（遵循“初始无权限、默认拒绝”原则）：

| 数据 | 内容 |
|------|------|
| 角色 | `超级管理员`（`SUPER_ADMIN`，level = 0） |
| 权限 | `user:create`、`user:read`、`user:update`、`user:delete`、`role:create`、`role:assign` |
| PA 关联 | 将上述 6 条权限授予 `SUPER_ADMIN` |

说明：

1. 脚本使用编码（`role_code` / `permission_code`）关联记录，不写死自增 ID，换库执行结果一致。
2. 管理员账号 `admin / 123456` 由后端 `DataInitializer` 在应用首次启动时创建，密码使用 BCrypt 动态加密，SQL 中不写死哈希值。
3. 仅执行 SQL 而不启动应用时，数据库中没有登录账号，属正常现象；启动应用后即可用 `admin / 123456` 登录。
4. 新创建的用户与角色默认不分配任何角色/权限，即无任何权限。

---

## 6. 与后端实现的对应关系

| 数据库对象 | 后端对应 |
|------------|----------|
| `sys_user` | `entity/User.java`（`@Table(name = "sys_user")`） |
| `sys_role` | `entity/Role.java`（`@Table(name = "sys_role")`） |
| `sys_permission` | `entity/Permission.java`（`@Table(name = "sys_permission")`） |
| `sys_user_role` | `User.roles` 上的 `@JoinTable(name = "sys_user_role")` |
| `sys_role_permission` | `Role.permissions` 上的 `@JoinTable(name = "sys_role_permission")` |
| 初始化数据 | `util/DataInitializer.java` |

JPA 采用默认命名策略，实体字段 `roleName`、`createdAt` 等自动映射为 `role_name`、`created_at`，与脚本中的列名一致。
后端配置 `ddl-auto: update`：表已存在时不会重建，仅补充缺失的表/列，因此手工执行本脚本与自动建表可以混用。

---

## 7. 执行结果验证

```sql
USE rbac;

-- 应返回 5 张表
SHOW TABLES;

-- 查看表结构
DESC sys_user;
DESC sys_role;
DESC sys_permission;
DESC sys_user_role;
DESC sys_role_permission;

-- 初始化数据校验：角色 1 条、权限 6 条、PA 关联 6 条
SELECT COUNT(*) FROM sys_role;
SELECT COUNT(*) FROM sys_permission;
SELECT COUNT(*) FROM sys_role_permission;

-- 鉴权链路自检：查询超级管理员拥有的全部权限
SELECT r.role_code, p.permission_code
FROM sys_role r
JOIN sys_role_permission rp ON rp.role_id = r.id
JOIN sys_permission p ON p.id = rp.permission_id
WHERE r.role_code = 'SUPER_ADMIN';
```

---

## 8. 注意事项

1. **重复执行安全**：两个脚本均为幂等脚本，可反复执行；不会删除表或已有数据。
2. **重置数据库**（谨慎，会清空数据）：如需重新初始化，可手工执行
   `DROP DATABASE IF EXISTS rbac;`，再按顺序重新执行两个脚本。
3. **字符集**：库与所有表统一使用 `utf8mb4`，支持中文与 emoji；请勿在库级使用 `latin1`。
4. **MySQL 版本**：脚本面向 MySQL 8.x；若使用 5.7，语法兼容，但默认字符集/collation 可能不同，建议显式指定 `utf8mb4`。
5. **时间字段**：`created_at` / `updated_at` 由数据库默认值与 `ON UPDATE` 维护，与 JPA 的 `@CreationTimestamp` / `@UpdateTimestamp` 语义一致。
