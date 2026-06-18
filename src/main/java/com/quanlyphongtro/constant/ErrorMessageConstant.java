package com.quanlyphongtro.constant;

public final class ErrorMessageConstant {
    public static final String GENERIC_ERROR = "Có lỗi xảy ra, vui lòng thử lại sau.";
    public static final String INVALID_CREDENTIALS = "Tên đăng nhập hoặc mật khẩu không chính xác.";
    public static final String ACCOUNT_LOCKED = "Tài khoản đã bị khóa tạm thời do nhập sai mật khẩu quá nhiều lần.";
    public static final String ACCOUNT_INACTIVE = "Tài khoản không hoạt động.";
    public static final String CSRF_INVALID = "Phiên làm việc không hợp lệ. Vui lòng thử lại.";
    public static final String FORBIDDEN = "Bạn không có quyền truy cập tài nguyên này.";
    public static final String NOT_FOUND = "Không tìm thấy dữ liệu yêu cầu.";

    private ErrorMessageConstant() {}
}
