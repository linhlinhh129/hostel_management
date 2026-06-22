package com.quanlyphongtro.util;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RateLimitManager {

    private static final int MAX_REQUESTS = 3;
    private static final long TIME_WINDOW_MS = 60 * 60 * 1000; // 1 hour

    private static class RateData {
        int count;
        long windowStartTime;

        public RateData(int count, long windowStartTime) {
            this.count = count;
            this.windowStartTime = windowStartTime;
        }
    }

    private static final Map<String, RateData> store = new ConcurrentHashMap<>();

    /**
     * Checks if the email is allowed to make another request.
     * @param email The email address to check.
     * @return true if allowed, false if limit exceeded.
     */
    public static synchronized boolean isAllowed(String email) {
        cleanUp();
        
        long now = System.currentTimeMillis();
        RateData data = store.get(email);

        if (data == null) {
            store.put(email, new RateData(1, now));
            return true;
        }

        if (now - data.windowStartTime > TIME_WINDOW_MS) {
            // Reset the window
            data.count = 1;
            data.windowStartTime = now;
            return true;
        }

        if (data.count < MAX_REQUESTS) {
            data.count++;
            return true;
        }

        return false;
    }

    private static void cleanUp() {
        long now = System.currentTimeMillis();
        Iterator<Map.Entry<String, RateData>> it = store.entrySet().iterator();
        while (it.hasNext()) {
            if (now - it.next().getValue().windowStartTime > TIME_WINDOW_MS) {
                it.remove();
            }
        }
    }
}
