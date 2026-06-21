package com.quanlyphongtro.util;

import jakarta.servlet.http.HttpSession;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class SessionRegistry {

    // Maps userId to a set of active HttpSessions
    private static final ConcurrentHashMap<Integer, Set<HttpSession>> userSessions = new ConcurrentHashMap<>();

    public static void addSession(int userId, HttpSession session) {
        userSessions.computeIfAbsent(userId, k -> Collections.newSetFromMap(new ConcurrentHashMap<>())).add(session);
    }

    public static void removeSession(int userId, HttpSession session) {
        Set<HttpSession> sessions = userSessions.get(userId);
        if (sessions != null) {
            sessions.remove(session);
            if (sessions.isEmpty()) {
                userSessions.remove(userId);
            }
        }
    }

    public static void invalidateAllSessions(int userId) {
        Set<HttpSession> sessions = userSessions.get(userId);
        if (sessions != null) {
            for (HttpSession session : sessions) {
                try {
                    session.invalidate();
                } catch (IllegalStateException e) {
                    // Session already invalidated
                }
            }
            userSessions.remove(userId);
        }
    }
}
