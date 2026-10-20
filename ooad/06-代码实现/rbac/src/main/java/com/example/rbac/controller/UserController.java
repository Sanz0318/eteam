package com.example.rbac.controller;

import com.example.rbac.common.ApiResponse;
import com.example.rbac.dto.UserRequest;
import com.example.rbac.entity.Role;
import com.example.rbac.entity.User;
import com.example.rbac.service.UserService;
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
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ApiResponse<List<User>> list() {
        return ApiResponse.ok(userService.list());
    }

    @GetMapping("/{id}")
    public ApiResponse<User> get(@PathVariable Long id) {
        return ApiResponse.ok(userService.get(id));
    }

    @PostMapping
    public ApiResponse<User> create(@RequestBody UserRequest req) {
        return ApiResponse.ok(userService.create(req));
    }

    @PutMapping("/{id}")
    public ApiResponse<User> update(@PathVariable Long id, @RequestBody UserRequest req) {
        return ApiResponse.ok(userService.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ApiResponse.ok();
    }

    @GetMapping("/{id}/roles")
    public ApiResponse<List<Role>> listRoles(@PathVariable Long id) {
        return ApiResponse.ok(userService.listRoles(id));
    }

    /** 整体设置用户角色（PUT 幂等；传 [] 清空所有角色 = 跨部门权限杀死重开） */
    @PutMapping("/{id}/roles")
    public ApiResponse<User> assignRoles(@PathVariable Long id, @RequestBody List<Long> roleIds) {
        return ApiResponse.ok(userService.assignRoles(id, roleIds));
    }

    @DeleteMapping("/{id}/roles/{roleId}")
    public ApiResponse<Void> removeRole(@PathVariable Long id, @PathVariable Long roleId) {
        userService.removeRole(id, roleId);
        return ApiResponse.ok();
    }
}
