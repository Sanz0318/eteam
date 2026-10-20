# 统一权限管理系统 — RBAC0（Spring Boot）

第一轮交付：RBAC0 核心框架。用户 →(UA)→ 角色 →(PA)→ 权限，两级多对多 + JWT 鉴权。

## 环境要求
- JDK 8 / 11（`pom.xml` 里 `java.version=11`，8 也兼容）
- Maven 3.6+
- MySQL 8.x

## 运行步骤
1. 建库（会自动建表，`ddl-auto: update`；也可手工执行 `04-数据库设计` 里的 SQL）：
   ```sql
   CREATE DATABASE rbac DEFAULT CHARACTER SET utf8mb4;
   ```
2. 改 `src/main/resources/application.yml` 里的数据库账号密码。
3. 启动：
   ```bash
   mvn spring-boot:run
   ```
4. 首次启动会自动创建示例数据：账号 `admin` / 密码 `123456`（超级管理员，拥有全部示例权限）。

## 测试接口
```bash
# 1. 登录拿 token
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"123456"}'

# 2. 鉴权检查（判断用户 1 是否有 user:create 权限）
curl "http://localhost:8080/api/auth/check?userId=1&permissionCode=user:create"

# 3. 带 token 调用管理接口（除 login/check 外都需认证）
curl http://localhost:8080/api/users \
  -H "Authorization: Bearer <上面返回的token>"
```

## 项目结构
```
com.example.rbac
├── RbacApplication.java      # 启动类
├── common/                   # 统一响应、业务异常、全局异常
├── config/WebConfig.java     # 拦截器注册
├── interceptor/              # 认证拦截器（校验 JWT）
├── util/                     # JwtUtil、DataInitializer
├── entity/                   # User / Role / Permission
├── repository/               # JPA 仓库（含鉴权 count 查询）
├── service/                  # Auth / User / Role / Permission
├── controller/               # REST 接口
└── dto/                      # 请求体
```

## 关键设计
- **默认拒绝**：鉴权 `countPermission` 未命中即返回 `allowed:false`（"初始无权限"）。
- **跨部门杀死重开**：`POST /api/users/{id}/roles` 传 `[]` 清空用户所有角色。
- **可自定义**：角色、权限、管理员等级（`role.level`）均为运行时数据，无硬编码。
- **生命周期**：`status` 字段控制启用/停用；停用用户鉴权立即拒绝。
- **并发**：鉴权为单条联表 COUNT 查询，后续可加 Redis 缓存扛早晚高峰。

## 说明
- 若换 Spring Boot 3.x（JDK 17+），把 `javax.*` 全部改成 `jakarta.*` 即可。
- 生产环境应对 `/api/auth/check` 增加系统级鉴权、密码改 BCrypt 已是标准做法、secret 放配置中心。
