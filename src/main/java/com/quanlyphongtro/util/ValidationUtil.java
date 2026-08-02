package com.quanlyphongtro.util;

import java.time.LocalDate;
import java.util.regex.Pattern;

public class ValidationUtil {
    // Regex cho số điện thoại Việt Nam: Bắt đầu bằng 0, +84 hoặc 84, tiếp theo là đầu số di động 3, 5, 7, 8, 9, tiếp theo là 8 số
    private static final Pattern VN_PHONE_PATTERN = Pattern.compile("^(0|\\+84|84)(3|5|7|8|9)[0-9]{8}$");
    
    // Regex cho CCCD Việt Nam: 12 số
    private static final Pattern VN_IDENTITY_PATTERN = Pattern.compile("^[0-9]{12}$");

    // Regex cho Họ và tên: Chỉ bao gồm chữ cái (có dấu tiếng Việt) và khoảng trắng
    private static final Pattern VN_NAME_PATTERN = Pattern.compile("^[\\p{L}\\s]+$", Pattern.UNICODE_CHARACTER_CLASS);

    /**
     * Kiểm tra Họ tên hợp lệ (chỉ bao gồm chữ cái và khoảng trắng, không chứa số hoặc ký tự đặc biệt).
     */
    public static boolean isValidFullName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        return VN_NAME_PATTERN.matcher(name.trim()).matches();
    }

    /**
     * Kiểm tra người dùng đã đủ 18 tuổi hay chưa.
     */
    public static boolean isAtLeast18YearsOld(LocalDate dob) {
        if (dob == null) {
            return false;
        }
        return !dob.plusYears(18).isAfter(LocalDate.now());
    }

    /**
     * Kiểm tra số điện thoại Việt Nam hợp lệ.
     */
    public static boolean isValidVnPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        return VN_PHONE_PATTERN.matcher(phone.trim()).matches();
    }

    /**
     * Kiểm tra số CMND/CCCD Việt Nam hợp lệ.
     */
    public static boolean isValidVnIdentity(String identityNumber) {
        if (identityNumber == null || identityNumber.trim().isEmpty()) {
            return false;
        }
        return VN_IDENTITY_PATTERN.matcher(identityNumber.trim()).matches();
    }

    /**
     * Kiểm tra định dạng đuôi file (SEC-07).
     * Chỉ chấp nhận: PDF, JPG, JPEG, PNG.
     */
    public static boolean isValidFileType(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            return false;
        }
        String lower = fileName.trim().toLowerCase();
        return lower.endsWith(".pdf") || lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".png");
    }

    /**
     * Kiểm tra kiểu MIME của file (SEC-07).
     */
    public static boolean isValidMimeType(String contentType) {
        if (contentType == null || contentType.trim().isEmpty()) {
            return false;
        }
        String lower = contentType.trim().toLowerCase();
        return "application/pdf".equals(lower) || 
               "image/jpeg".equals(lower) || 
               "image/jpg".equals(lower) || 
               "image/png".equals(lower);
    }
}
