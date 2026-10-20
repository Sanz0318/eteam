import { reactive } from 'vue'
import type { User, Role, Permission } from '../types'

/**
 * 静态原型用的 Mock 数据（内存态，刷新即恢复初始值）。
 * 第二轮接入后端时，把这些操作替换为 axios 调用即可。
 */
export const store = reactive({
  users: [
    { id: 1, username: 'admin', nickname: '管理员', status: 1, roleIds: [1] },
    { id: 2, username: 'zhangsan', nickname: '张三', status: 1, roleIds: [2] },
    { id: 3, username: 'lisi', nickname: '李四', status: 0, roleIds: [] },
  ] as User[],

  roles: [
    { id: 1, roleName: '超级管理员', roleCode: 'SUPER_ADMIN', level: 0, description: '系统内置最高管理员', status: 1, permissionIds: [1, 2, 3, 4, 5, 6] },
    { id: 2, roleName: '财务专员', roleCode: 'FINANCE', level: 2, description: '财务相关操作', status: 1, permissionIds: [2] },
    { id: 3, roleName: '普通员工', roleCode: 'STAFF', level: 3, description: '', status: 1, permissionIds: [] },
  ] as Role[],

  permissions: [
    { id: 1, permissionName: '创建用户', resource: 'user', operation: 'create', permissionCode: 'user:create' },
    { id: 2, permissionName: '查看用户', resource: 'user', operation: 'read', permissionCode: 'user:read' },
    { id: 3, permissionName: '修改用户', resource: 'user', operation: 'update', permissionCode: 'user:update' },
    { id: 4, permissionName: '删除用户', resource: 'user', operation: 'delete', permissionCode: 'user:delete' },
    { id: 5, permissionName: '创建角色', resource: 'role', operation: 'create', permissionCode: 'role:create' },
    { id: 6, permissionName: '分配角色权限', resource: 'role', operation: 'assign', permissionCode: 'role:assign' },
  ] as Permission[],
})

/** 根据 roleIds 取角色名称拼接 */
export function roleNames(roleIds: number[]): string {
  const names = store.roles.filter(r => roleIds.includes(r.id)).map(r => r.roleName)
  return names.length ? names.join('、') : '—'
}

/** 生成自增 id */
export function nextId(list: { id: number }[]): number {
  return list.length ? Math.max(...list.map(x => x.id)) + 1 : 1
}
