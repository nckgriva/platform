package com.gracelogic.platform.user.service;

import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class LastSessionHolder {
    private static Map<UUID, String> actualSessionIds = new ConcurrentHashMap<UUID, String>();

    public static void updateLastSessionSessionId(UUID userId, String sessionId) {
        actualSessionIds.put(userId, sessionId);
    }

    public static boolean isLastSession(UUID userId, String sessionId) {
        String actualSessionId = actualSessionIds.get(userId);
        if (actualSessionId != null && StringUtils.equalsIgnoreCase(sessionId, actualSessionId)) {
            return true;
        }
        return false;
    }
}
