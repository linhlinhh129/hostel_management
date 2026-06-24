package com.quanlyphongtro.util;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

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

    // Argon2id configuration (m=65536, t=3, p=4)
    private static final int MEMORY = 65536;
    private static final int ITERATIONS = 3;
    private static final int PARALLELISM = 4;
    private static final Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);

    /**
     * Hash password with Argon2id.
     */
    public static String hash(String plainPassword) {
        return argon2.hash(ITERATIONS, MEMORY, PARALLELISM, plainPassword.toCharArray());
    }

    /**
     * Verify plain password against Argon2id hash.
     */
    public static boolean verify(String plainPassword, String hashed) {
        if (plainPassword == null || hashed == null) return false;
        return argon2.verify(hashed, plainPassword.toCharArray());
    }
}
