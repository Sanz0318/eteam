package com.example.rbac.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 用户创建/修改入参（修改时只传需要变更的字段）
 */
@Getter
@Setter
public class UserRequest {
    private String username;
    private String password;
    private String nickname;
    private Integer status;
}
