package com.example.rbac.common;

import lombok.Getter;

/**
 * 业务异常，携带错误码
 */
@Getter
public class BusinessException extends RuntimeException {
    private final int code;

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }
}
