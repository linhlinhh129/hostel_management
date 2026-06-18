package com.quanlyphongtro.exception;

public class NotFoundException extends AppException {
    public NotFoundException() {
        super("Không tìm thấy dữ liệu yêu cầu.");
    }

    public NotFoundException(String message) {
        super(message);
    }
}
