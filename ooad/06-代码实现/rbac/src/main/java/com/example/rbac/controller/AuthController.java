package com.example.rbac.controller;

import com.example.rbac.common.ApiResponse;
import com.example.rbac.dto.LoginRequest;
import com.example.rbac.service.AuthService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ApiResponse<Map<String, Object>> login(@RequestBody LoginRequest req) {
        String token = authService.login(req.getUsername(), req.getPassword());
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        return ApiResponse.ok(data);
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout() {
        return ApiResponse.ok();
    }

    /** 鉴权检查（接入系统调用）：只读查询，用 GET + query 参数 */
    @GetMapping("/check")
    public ApiResponse<Map<String, Boolean>> check(@RequestParam Long userId,
                                                   @RequestParam String permissionCode) {
        boolean allowed = authService.check(userId, permissionCode);
        Map<String, Boolean> data = new HashMap<>();
        data.put("allowed", allowed);
        return ApiResponse.ok(data);
    }
}
