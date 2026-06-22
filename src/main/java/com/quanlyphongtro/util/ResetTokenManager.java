package com.quanlyphongtro.util;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ResetTokenManager {

    private static final long TOKEN_TTL_MS = 15 * 60 * 1000; // 15 minutes
    
    private static class TokenData {
        int userId;
        long expiryTime;

        public TokenData(int userId, long expiryTime) {
            this.userId = userId;
            this.expiryTime = expiryTime;
        }
    }

    private static final Map<String, TokenData> tokenStore = new ConcurrentHashMap<>();

    public static String generateToken(int userId) {
        cleanUp();
        String token = UUID.randomUUID().toString();
        long expiryTime = System.currentTimeMillis() + TOKEN_TTL_MS;
        tokenStore.put(token, new TokenData(userId, expiryTime));
        return token;
    }

    public static Integer verifyToken(String token) {
        cleanUp();
        TokenData data = tokenStore.get(token);
        if (data != null && data.expiryTime > System.currentTimeMillis()) {
            return data.userId;
        }
        return null;
    }

    public static void invalidateToken(String token) {
        tokenStore.remove(token);
    }

    private static void cleanUp() {
        long now = System.currentTimeMillis();
        Iterator<Map.Entry<String, TokenData>> it = tokenStore.entrySet().iterator();
        while (it.hasNext()) {
            if (it.next().getValue().expiryTime <= now) {
                it.remove();
            }
        }
    }
}
