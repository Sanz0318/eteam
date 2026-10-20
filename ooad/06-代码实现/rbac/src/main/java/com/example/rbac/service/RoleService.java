package com.example.rbac.service;

import com.example.rbac.common.BusinessException;
import com.example.rbac.dto.RoleRequest;
import com.example.rbac.entity.Permission;
import com.example.rbac.entity.Role;
import com.example.rbac.repository.PermissionRepository;
import com.example.rbac.repository.RoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public RoleService(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    public List<Role> list() {
        return roleRepository.findAll();
    }

    public Role get(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "角色不存在"));
    }

    @Transactional
    public Role create(RoleRequest req) {
        if (roleRepository.findByRoleCode(req.getRoleCode()).isPresent()) {
            throw new BusinessException(400, "角色编码已存在");
        }
        Role r = new Role();
        r.setRoleName(req.getRoleName());
        r.setRoleCode(req.getRoleCode());
        r.setLevel(req.getLevel() != null ? req.getLevel() : 0);
        r.setDescription(req.getDescription());
        r.setStatus(1);
        return roleRepository.save(r);
    }

    @Transactional
    public Role update(Long id, RoleRequest req) {
        Role r = get(id);
        if (req.getRoleName() != null) r.setRoleName(req.getRoleName());
        if (req.getLevel() != null) r.setLevel(req.getLevel());
        if (req.getDescription() != null) r.setDescription(req.getDescription());
        if (req.getStatus() != null) r.setStatus(req.getStatus());
        return roleRepository.save(r);
    }

    @Transactional
    public void delete(Long id) {
        roleRepository.deleteById(id);
    }

    /** 给角色分配权限 */
    @Transactional
    public Role assignPermissions(Long roleId, List<Long> permissionIds) {
        Role r = get(roleId);
        Set<Permission> perms = new HashSet<>(permissionRepository.findAllById(permissionIds));
        r.setPermissions(perms);
        return roleRepository.save(r);
    }

    @Transactional
    public void removePermission(Long roleId, Long permissionId) {
        Role r = get(roleId);
        r.getPermissions().removeIf(p -> p.getId().equals(permissionId));
        roleRepository.save(r);
    }

    @Transactional(readOnly = true)
    public List<Permission> listPermissions(Long roleId) {
        return new ArrayList<>(get(roleId).getPermissions());
    }
}
