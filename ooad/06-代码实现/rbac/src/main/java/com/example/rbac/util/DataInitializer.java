package com.example.rbac.util;

import com.example.rbac.entity.Permission;
import com.example.rbac.entity.Role;
import com.example.rbac.entity.User;
import com.example.rbac.repository.PermissionRepository;
import com.example.rbac.repository.RoleRepository;
import com.example.rbac.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * 初始化数据：首次启动时创建示例权限、超级管理员角色和 admin 账号（admin/123456）
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public DataInitializer(UserRepository userRepository, RoleRepository roleRepository,
                           PermissionRepository permissionRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        List<Permission> perms = Arrays.asList(
                ensurePermission("创建用户", "user", "create", "user:create"),
                ensurePermission("查看用户", "user", "read", "user:read"),
                ensurePermission("修改用户", "user", "update", "user:update"),
                ensurePermission("删除用户", "user", "delete", "user:delete"),
                ensurePermission("创建角色", "role", "create", "role:create"),
                ensurePermission("分配角色权限", "role", "assign", "role:assign")
        );

        Role adminRole = ensureRole("超级管理员", "SUPER_ADMIN", 0);
        adminRole.getPermissions().addAll(perms);
        roleRepository.save(adminRole);

        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("123456"));
            admin.setNickname("管理员");
            admin.setStatus(1);
            admin.setRoles(new HashSet<>(Arrays.asList(adminRole)));
            userRepository.save(admin);
        }
    }

    private Permission ensurePermission(String name, String resource, String op, String code) {
        return permissionRepository.findByPermissionCode(code).orElseGet(() -> {
            Permission p = new Permission();
            p.setPermissionName(name);
            p.setResource(resource);
            p.setOperation(op);
            p.setPermissionCode(code);
            return permissionRepository.save(p);
        });
    }

    private Role ensureRole(String name, String code, int level) {
        return roleRepository.findByRoleCode(code).orElseGet(() -> {
            Role r = new Role();
            r.setRoleName(name);
            r.setRoleCode(code);
            r.setLevel(level);
            r.setStatus(1);
            return roleRepository.save(r);
        });
    }
}
