package com.example.rbac.service;

import com.example.rbac.common.BusinessException;
import com.example.rbac.dto.PermissionRequest;
import com.example.rbac.entity.Permission;
import com.example.rbac.repository.PermissionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PermissionService {

    private final PermissionRepository permissionRepository;

    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public List<Permission> list() {
        return permissionRepository.findAll();
    }

    public Permission get(Long id) {
        return permissionRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "权限不存在"));
    }

    @Transactional
    public Permission create(PermissionRequest req) {
        if (permissionRepository.findByPermissionCode(req.getPermissionCode()).isPresent()) {
            throw new BusinessException(400, "权限编码已存在");
        }
        Permission p = new Permission();
        p.setPermissionName(req.getPermissionName());
        p.setResource(req.getResource());
        p.setOperation(req.getOperation());
        p.setPermissionCode(req.getPermissionCode());
        p.setDescription(req.getDescription());
        return permissionRepository.save(p);
    }

    @Transactional
    public Permission update(Long id, PermissionRequest req) {
        Permission p = get(id);
        if (req.getPermissionName() != null) p.setPermissionName(req.getPermissionName());
        if (req.getResource() != null) p.setResource(req.getResource());
        if (req.getOperation() != null) p.setOperation(req.getOperation());
        if (req.getDescription() != null) p.setDescription(req.getDescription());
        return permissionRepository.save(p);
    }

    @Transactional
    public void delete(Long id) {
        permissionRepository.deleteById(id);
    }
}
