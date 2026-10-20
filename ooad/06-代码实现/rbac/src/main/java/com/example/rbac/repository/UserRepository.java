package com.example.rbac.repository;

import com.example.rbac.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    /**
     * 鉴权核心查询：用户 ->(UA)-> 角色 ->(PA)-> 权限，命中目标权限码则计数 > 0
     */
    @Query("SELECT COUNT(u) FROM User u JOIN u.roles r JOIN r.permissions p " +
            "WHERE u.id = :userId AND p.permissionCode = :code")
    long countPermission(@Param("userId") Long userId, @Param("code") String code);
}
