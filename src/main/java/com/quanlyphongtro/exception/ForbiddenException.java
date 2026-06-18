package com.quanlyphongtro.exception;

public class ForbiddenException extends AppException {
    public ForbiddenException() {
        super("Bạn không có quyền truy cập tài nguyên này.");
    }

    public ForbiddenException(String message) {
        super(message);
    }
}
