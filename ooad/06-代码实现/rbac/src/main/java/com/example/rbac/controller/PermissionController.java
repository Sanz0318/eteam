package com.example.rbac.controller;

import com.example.rbac.common.ApiResponse;
import com.example.rbac.dto.PermissionRequest;
import com.example.rbac.entity.Permission;
import com.example.rbac.service.PermissionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/permissions")
public class PermissionController {

    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @GetMapping
    public ApiResponse<List<Permission>> list() {
        return ApiResponse.ok(permissionService.list());
    }

    @GetMapping("/{id}")
    public ApiResponse<Permission> get(@PathVariable Long id) {
        return ApiResponse.ok(permissionService.get(id));
    }

    @PostMapping
    public ApiResponse<Permission> create(@RequestBody PermissionRequest req) {
        return ApiResponse.ok(permissionService.create(req));
    }

    @PutMapping("/{id}")
    public ApiResponse<Permission> update(@PathVariable Long id, @RequestBody PermissionRequest req) {
        return ApiResponse.ok(permissionService.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        permissionService.delete(id);
        return ApiResponse.ok();
    }
}
