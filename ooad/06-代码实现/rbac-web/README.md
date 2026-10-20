# 统一权限管理系统 — 界面原型（Vue3 + TS + Tailwind）

第一轮交付：静态界面原型。数据为内存 Mock，刷新即恢复初始值；第二轮接入后端 API。

## 环境要求
- Node.js 18+
- npm / pnpm / yarn

## 运行
```bash
npm install
npm run dev        # 打开 http://localhost:5173
```

构建生产包：`npm run build`，预览：`npm run preview`。

## 页面
- `/login` 登录页（演示账号 admin / 123456，不校验直接进入）
- `/users` 用户管理（增删改 + 分配角色穿梭框）
- `/roles` 角色管理（增删改 + 可自定义等级 + 分配权限穿梭框）
- `/permissions` 权限管理（增删改，权限 = 资源 + 操作）

## 结构
```
src/
├── views/          页面
├── components/     Modal 弹窗、Transfer 穿梭框
├── router/         路由
├── mock/data.ts    内存 Mock 数据（reactive 单例）
└── types/          TS 类型
```

## 接入后端（第二轮）
`src/mock/data.ts` 的 `store` 是唯一数据源，接入时：
1. 新增 `src/api/` 封装 axios 请求。
2. 把各 view 里对 `store` 的增删改查替换为 `api` 调用（接口见 `03-API设计`）。
3. 登录改为调 `POST /api/auth/login` 并保存 token。
