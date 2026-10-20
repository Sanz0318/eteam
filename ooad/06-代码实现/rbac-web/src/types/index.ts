export interface Role {
  id: number
  roleName: string
  roleCode: string
  level: number
  description?: string
  status: number
  /** 已分配的权限 id（原型用，后端对应 role_permission 关联表） */
  permissionIds?: number[]
}

export interface Permission {
  id: number
  permissionName: string
  resource: string
  operation: string
  permissionCode: string
  description?: string
}

export interface User {
  id: number
  username: string
  nickname: string
  status: number
  roleIds: number[]
}
