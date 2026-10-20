package com.example.rbac.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JWT 工具单元测试
 */
class JwtUtilTest {

    private JwtUtil newJwtUtil() {
        JwtUtil jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", "rbac-demo-secret-key-please-change-this-32bytes!");
        ReflectionTestUtils.setField(jwtUtil, "expireHours", 24L);
        return jwtUtil;
    }

    @Test
    @DisplayName("生成后能解析回 userId")
    void generate_and_parse_roundtrip() {
        JwtUtil jwtUtil = newJwtUtil();

        String token = jwtUtil.generate(1L, "admin");

        assertEquals(1L, jwtUtil.parseUserId(token));
    }

    @Test
    @DisplayName("非法 token 抛异常")
    void parse_invalid_token_throws() {
        JwtUtil jwtUtil = newJwtUtil();

        assertThrows(Exception.class, () -> jwtUtil.parseUserId("invalid-token"));
    }
}
