package com.example.rbac.service;

import com.example.rbac.common.BusinessException;
import com.example.rbac.entity.User;
import com.example.rbac.repository.UserRepository;
import com.example.rbac.util.JwtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * 认证与鉴权单元测试
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    private User buildUser(String rawPassword, int status) {
        User u = new User();
        u.setId(1L);
        u.setUsername("admin");
        u.setPassword(new BCryptPasswordEncoder().encode(rawPassword));
        u.setStatus(status);
        return u;
    }

    @Test
    @DisplayName("登录成功返回 token")
    void login_returns_token() {
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(buildUser("123456", 1)));
        when(jwtUtil.generate(1L, "admin")).thenReturn("mock-token");

        assertEquals("mock-token", authService.login("admin", "123456"));
    }

    @Test
    @DisplayName("密码错误抛 401")
    void login_wrong_password_throws() {
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(buildUser("correct", 1)));

        BusinessException e = assertThrows(BusinessException.class,
                () -> authService.login("admin", "wrong"));
        assertEquals(401, e.getCode());
    }

    @Test
    @DisplayName("停用账号抛 401")
    void login_stopped_user_throws() {
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(buildUser("123456", 0)));

        assertThrows(BusinessException.class, () -> authService.login("admin", "123456"));
    }

    @Test
    @DisplayName("鉴权命中权限返回 true")
    void check_hit_returns_true() {
        when(userRepository.countPermission(1L, "user:create")).thenReturn(1L);
        assertTrue(authService.check(1L, "user:create"));
    }

    @Test
    @DisplayName("鉴权未命中默认拒绝返回 false")
    void check_miss_returns_false() {
        when(userRepository.countPermission(1L, "order:delete")).thenReturn(0L);
        assertFalse(authService.check(1L, "order:delete"));
    }

    @Test
    @DisplayName("鉴权空参数返回 false")
    void check_null_args_returns_false() {
        assertFalse(authService.check(null, "user:create"));
        assertFalse(authService.check(1L, null));
    }
}
