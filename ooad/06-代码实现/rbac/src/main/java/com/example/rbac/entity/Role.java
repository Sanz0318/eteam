package com.example.rbac.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * 角色（RBAC0 的 Role），level 为可自定义的管理员等级
 */
@Getter
@Setter
@Entity
@Table(name = "sys_role")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String roleName;

    @Column(nullable = false, unique = true)
    private String roleCode;

    /** 管理员等级（可自定义，数值越小等级越高） */
    private Integer level = 0;

    private String description;

    /** 状态：1 启用，0 停用 */
    @Column(nullable = false)
    private Integer status = 1;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    /** 角色-权限 多对多（PA） */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "sys_role_permission",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id"))
    private Set<Permission> permissions = new HashSet<>();
}
