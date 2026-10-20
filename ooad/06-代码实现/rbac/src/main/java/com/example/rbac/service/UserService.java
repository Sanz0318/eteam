package com.example.rbac.service;

import com.example.rbac.common.BusinessException;
import com.example.rbac.dto.UserRequest;
import com.example.rbac.entity.Role;
import com.example.rbac.entity.User;
import com.example.rbac.repository.RoleRepository;
import com.example.rbac.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    public List<User> list() {
        return userRepository.findAll();
    }

    public User get(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "用户不存在"));
    }

    @Transactional
    public User create(UserRequest req) {
        if (userRepository.findByUsername(req.getUsername()).isPresent()) {
            throw new BusinessException(400, "账号已存在");
        }
        User u = new User();
        u.setUsername(req.getUsername());
        u.setPassword(passwordEncoder.encode(req.getPassword()));
        u.setNickname(req.getNickname());
        u.setStatus(1);
        return userRepository.save(u);
    }

    @Transactional
    public User update(Long id, UserRequest req) {
        User u = get(id);
        if (req.getNickname() != null) u.setNickname(req.getNickname());
        if (req.getPassword() != null && !req.getPassword().isEmpty()) {
            u.setPassword(passwordEncoder.encode(req.getPassword()));
        }
        if (req.getStatus() != null) u.setStatus(req.getStatus());
        return userRepository.save(u);
    }

    @Transactional
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    /** 给用户分配角色（传空列表即"清空所有角色"，对应跨部门权限杀死重开） */
    @Transactional
    public User assignRoles(Long userId, List<Long> roleIds) {
        User u = get(userId);
        Set<Role> roles = new HashSet<>(roleRepository.findAllById(roleIds));
        u.setRoles(roles);
        return userRepository.save(u);
    }

    @Transactional
    public void removeRole(Long userId, Long roleId) {
        User u = get(userId);
        u.getRoles().removeIf(r -> r.getId().equals(roleId));
        userRepository.save(u);
    }

    @Transactional(readOnly = true)
    public List<Role> listRoles(Long userId) {
        return new ArrayList<>(get(userId).getRoles());
    }
}
