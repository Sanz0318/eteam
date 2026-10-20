package com.example.rbac.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 权限（RBAC0 的 Permission）= 资源 + 操作
 */
@Getter
@Setter
@Entity
@Table(name = "sys_permission")
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String permissionName;

    /** 资源，如 order */
    @Column(nullable = false)
    private String resource;

    /** 操作，如 delete */
    @Column(nullable = false)
    private String operation;

    /** 权限编码，如 order:delete */
    @Column(nullable = false, unique = true)
    private String permissionCode;

    private String description;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
