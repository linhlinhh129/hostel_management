package com.quanlyphongtro.util;

import org.mindrot.jbcrypt.BCrypt;

import java.security.SecureRandom;

public final class PasswordUtil {
    private PasswordUtil() {}

    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL = "@#$%!";
    private static final String ALL = UPPER + LOWER + DIGITS + SPECIAL;
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Sinh mật khẩu tạm thời 12 ký tự gồm hoa/thường/số/ký tự đặc biệt.
     */
    public static String generateTempPassword() {
        char[] pwd = new char[12];
        pwd[0] = UPPER.charAt(RANDOM.nextInt(UPPER.length()));
        pwd[1] = LOWER.charAt(RANDOM.nextInt(LOWER.length()));
        pwd[2] = DIGITS.charAt(RANDOM.nextInt(DIGITS.length()));
        pwd[3] = SPECIAL.charAt(RANDOM.nextInt(SPECIAL.length()));
        for (int i = 4; i < 12; i++) {
            pwd[i] = ALL.charAt(RANDOM.nextInt(ALL.length()));
        }
        // Shuffle để không lộ vị trí cố định
        for (int i = 11; i > 0; i--) {
            int j = RANDOM.nextInt(i + 1);
            char tmp = pwd[i]; pwd[i] = pwd[j]; pwd[j] = tmp;
        }
        return new String(pwd);
    }

    /**
     * Hash password with BCrypt.
     */
    public static String hash(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(10));
    }

    /**
     * Verify plain password against BCrypt hash.
     */
    public static boolean verify(String plainPassword, String hashed) {
        if (plainPassword == null || hashed == null) return false;
        try {
            return BCrypt.checkpw(plainPassword, hashed);
        } catch (Exception e) {
            return false;
        }
    }
}
