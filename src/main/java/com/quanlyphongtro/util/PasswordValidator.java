package com.quanlyphongtro.util;

import com.quanlyphongtro.exception.ValidationException;

import java.util.regex.Pattern;

public class PasswordValidator {

    // Từ 8 đến 50 ký tự, ít nhất 1 chữ hoa, 1 chữ số, 1 ký tự đặc biệt
    private static final String PASSWORD_PATTERN = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[@#$%^&+=!]).{8,50}$";
    private static final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);

    public static final String POLICY_MESSAGE = "Mật khẩu phải từ 8 đến 50 ký tự, bao gồm ít nhất 1 chữ hoa, 1 chữ số và 1 ký tự đặc biệt.";
    public static final String HTML_PATTERN = "(?=.*[A-Z])(?=.*[0-9])(?=.*[@#$%^&+=!]).{8,50}";

    public static boolean isValid(String password) {
        if (password == null) {
            return false;
        }
        return pattern.matcher(password).matches();
    }

    public static void validate(String password) throws ValidationException {
        if (!isValid(password)) {
            throw new ValidationException(POLICY_MESSAGE);
        }
    }
}
