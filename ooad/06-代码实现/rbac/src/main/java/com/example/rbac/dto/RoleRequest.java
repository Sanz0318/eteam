package com.example.rbac.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 角色创建/修改入参（修改时只传需要变更的字段）
 */
@Getter
@Setter
public class RoleRequest {
    private String roleName;
    private String roleCode;
    private Integer level;
    private String description;
    private Integer status;
}
