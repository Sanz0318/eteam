package com.example.rbac.controller;

import com.example.rbac.common.ApiResponse;
import com.example.rbac.dto.RoleRequest;
import com.example.rbac.entity.Permission;
import com.example.rbac.entity.Role;
import com.example.rbac.service.RoleService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    public ApiResponse<List<Role>> list() {
        return ApiResponse.ok(roleService.list());
    }

    @GetMapping("/{id}")
    public ApiResponse<Role> get(@PathVariable Long id) {
        return ApiResponse.ok(roleService.get(id));
    }

    @PostMapping
    public ApiResponse<Role> create(@RequestBody RoleRequest req) {
        return ApiResponse.ok(roleService.create(req));
    }

    @PutMapping("/{id}")
    public ApiResponse<Role> update(@PathVariable Long id, @RequestBody RoleRequest req) {
        return ApiResponse.ok(roleService.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        roleService.delete(id);
        return ApiResponse.ok();
    }

    @GetMapping("/{id}/permissions")
    public ApiResponse<List<Permission>> listPermissions(@PathVariable Long id) {
        return ApiResponse.ok(roleService.listPermissions(id));
    }

    /** 整体设置角色权限（PUT 幂等） */
    @PutMapping("/{id}/permissions")
    public ApiResponse<Role> assignPermissions(@PathVariable Long id, @RequestBody List<Long> permissionIds) {
        return ApiResponse.ok(roleService.assignPermissions(id, permissionIds));
    }

    @DeleteMapping("/{id}/permissions/{permissionId}")
    public ApiResponse<Void> removePermission(@PathVariable Long id, @PathVariable Long permissionId) {
        roleService.removePermission(id, permissionId);
        return ApiResponse.ok();
    }
}
