package com.quanlyphongtro.util;

import com.quanlyphongtro.constant.RoleConstant;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

public final class LoginAttemptTracker {
    private static final ConcurrentHashMap<String, AttemptRecord> ATTEMPTS = new ConcurrentHashMap<>();

    private LoginAttemptTracker() {}

    public static boolean isLocked(String username) {
        AttemptRecord record = ATTEMPTS.get(username);
        if (record == null || record.lockedUntil == null) {
            return false;
        }
        if (LocalDateTime.now().isBefore(record.lockedUntil)) {
            return true;
        }
        ATTEMPTS.remove(username);
        return false;
    }

    public static int recordFailure(String username) {
        AttemptRecord record = ATTEMPTS.computeIfAbsent(username, k -> new AttemptRecord());
        record.count++;
        if (record.count >= RoleConstant.MAX_LOGIN_ATTEMPTS) {
            record.lockedUntil = LocalDateTime.now().plusMinutes(RoleConstant.LOCK_DURATION_MINUTES);
        }
        return record.count;
    }

    public static void reset(String username) {
        ATTEMPTS.remove(username);
    }

    private static final class AttemptRecord {
        private int count;
        private LocalDateTime lockedUntil;
    }
}
