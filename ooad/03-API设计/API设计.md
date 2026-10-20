# 统一权限管理系统 — API 设计（RESTful）

> 风格：RESTful + JSON。资源用名词复数，方法语义化。
> 认证：登录返回 JWT，请求头 `Authorization: Bearer <token>`（除 login/check 外均需携带）。
> 统一响应体：`{ "code": 200, "message": "ok", "data": ... }`。

---

## 1. RESTful 约定

| 约定 | 说明 |
|------|------|
| 资源用名词复数 | `/api/users`、`/api/roles`、`/api/permissions` |
| 方法语义 | GET 查 / POST 增 / PUT 整体替换(幂等) / DELETE 删 |
| 幂等性 | GET / PUT / DELETE 幂等；POST 非幂等 |
| 嵌套子资源 | 用户-角色、角色-权限 用嵌套路径表达关联 |
| 状态码 | 200 成功、400 参数/业务错误、401 未认证、403 无权限、404 不存在、500 服务错误 |
| 特例 | 登录/登出/鉴权检查属认证动作，业界惯例放 `/api/auth/*` |

---

## 2. 认证模块 `/api/auth`

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/auth/login` | 登录，body `{username, password}` → `data.token` |
| POST | `/api/auth/logout` | 登出 |
| GET | `/api/auth/check` | 鉴权检查（只读），query：`?userId=1&permissionCode=user:create` → `data.allowed` |

> 说明：`check` 是只读查询故用 GET + query 参数；`login/logout` 是认证动作，属 REST 例外。

---

## 3. 用户管理 `/api/users`

| 方法 | 路径 | 说明 | 入参 |
|------|------|------|------|
| GET | `/api/users` | 用户列表 | — |
| POST | `/api/users` | 新增用户 | `{username, password, nickname}` |
| GET | `/api/users/{id}` | 用户详情 | — |
| PUT | `/api/users/{id}` | 修改用户 | `{nickname, status, password?}` |
| DELETE | `/api/users/{id}` | 删除用户 | — |

### 3.1 用户-角色（嵌套子资源）
| 方法 | 路径 | 说明 | 入参 |
|------|------|------|------|
| GET | `/api/users/{id}/roles` | 查询用户角色 | — |
| PUT | `/api/users/{id}/roles` | 整体设置角色（传 `[]` 清空） | `[1,2]` |
| DELETE | `/api/users/{id}/roles/{roleId}` | 移除单个角色 | — |

## 4. 角色管理 `/api/roles`

| 方法 | 路径 | 说明 | 入参 |
|------|------|------|------|
| GET | `/api/roles` | 角色列表 | — |
| POST | `/api/roles` | 新增角色 | `{roleName, roleCode, level, description}` |
| GET | `/api/roles/{id}` | 角色详情 | — |
| PUT | `/api/roles/{id}` | 修改角色 | `{roleName, level, description, status}` |
| DELETE | `/api/roles/{id}` | 删除角色 | — |

### 4.1 角色-权限（嵌套子资源）
| 方法 | 路径 | 说明 | 入参 |
|------|------|------|------|
| GET | `/api/roles/{id}/permissions` | 查询角色权限 | — |
| PUT | `/api/roles/{id}/permissions` | 整体设置权限 | `[1,2]` |
| DELETE | `/api/roles/{id}/permissions/{permissionId}` | 移除单个权限 | — |

## 5. 权限管理 `/api/permissions`

| 方法 | 路径 | 说明 | 入参 |
|------|------|------|------|
| GET | `/api/permissions` | 权限列表 | — |
| POST | `/api/permissions` | 新增权限 | `{permissionName, resource, operation, permissionCode}` |
| GET | `/api/permissions/{id}` | 权限详情 | — |
| PUT | `/api/permissions/{id}` | 修改权限 | `{permissionName, resource, operation, description}` |
| DELETE | `/api/permissions/{id}` | 删除权限 | — |

---

## 6. 请求/响应示例

### 登录
```bash
POST /api/auth/login
{ "username": "admin", "password": "123456" }
→ { "code": 200, "message": "ok", "data": { "token": "<JWT>" } }
```

### 鉴权检查
```bash
GET /api/auth/check?userId=1&permissionCode=user:create
→ { "code": 200, "message": "ok", "data": { "allowed": true } }
```

### 整体设置用户角色
```bash
PUT /api/users/2/roles
[1, 3]
→ { "code": 200, "message": "ok", "data": { ...user } }
```

---

## 7. 关键说明

1. **默认拒绝**：鉴权未命中即 `allowed:false`（"初始无权限"）。
2. **鉴权路径**：`用户 →(UA)→ 角色 →(PA)→ 权限`，两级多对多联表查询。
3. **跨部门回收**：`PUT /api/users/{id}/roles` 传 `[]` 清空全部角色 = "权限直接杀死"。
4. **并发**：鉴权为单条联表 COUNT，可加缓存扛早晚高峰。
5. **生命周期**：删除/停用角色后，其权限对用户即时失效。
