package com.example.rbac.service;

import com.example.rbac.common.BusinessException;
import com.example.rbac.entity.User;
import com.example.rbac.repository.UserRepository;
import com.example.rbac.util.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 认证与鉴权
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthService(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    public String login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(401, "账号或密码错误"));
        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new BusinessException(401, "账号已停用");
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BusinessException(401, "账号或密码错误");
        }
        return jwtUtil.generate(user.getId(), user.getUsername());
    }

    /**
     * 鉴权检查（默认拒绝：未命中即 false）
     */
    @Transactional(readOnly = true)
    public boolean check(Long userId, String permissionCode) {
        if (userId == null || permissionCode == null) {
            return false;
        }
        return userRepository.countPermission(userId, permissionCode) > 0;
    }
}
