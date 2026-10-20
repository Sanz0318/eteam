package com.example.rbac.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 权限创建/修改入参（修改时只传需要变更的字段）
 */
@Getter
@Setter
public class PermissionRequest {
    private String permissionName;
    private String resource;
    private String operation;
    private String permissionCode;
    private String description;
}
