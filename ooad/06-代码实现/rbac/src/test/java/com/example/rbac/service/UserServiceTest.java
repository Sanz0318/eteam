package com.example.rbac.service;

import com.example.rbac.common.BusinessException;
import com.example.rbac.dto.UserRequest;
import com.example.rbac.entity.User;
import com.example.rbac.repository.RoleRepository;
import com.example.rbac.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

/**
 * 用户管理单元测试
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("账号已存在抛 400")
    void create_duplicate_username_throws() {
        User existing = new User();
        existing.setUsername("admin");
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(existing));

        UserRequest req = new UserRequest();
        req.setUsername("admin");

        BusinessException e = assertThrows(BusinessException.class, () -> userService.create(req));
        assertEquals(400, e.getCode());
    }

    @Test
    @DisplayName("创建用户时密码被 BCrypt 加密")
    void create_password_is_encoded() {
        when(userRepository.findByUsername("zhang")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        UserRequest req = new UserRequest();
        req.setUsername("zhang");
        req.setPassword("123456");
        req.setNickname("张三");

        User result = userService.create(req);

        assertNotEquals("123456", result.getPassword());
        assertTrue(result.getPassword().startsWith("$2"), "应为 BCrypt 密文");
        assertEquals(1, result.getStatus());
    }

    @Test
    @DisplayName("分配角色传空列表 = 清空所有角色（跨部门杀死重开）")
    void assign_roles_empty_clears_all() {
        User user = new User();
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(roleRepository.findAllById(anyList())).thenReturn(Collections.emptyList());
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User result = userService.assignRoles(1L, Collections.emptyList());

        assertTrue(result.getRoles().isEmpty());
    }
}
