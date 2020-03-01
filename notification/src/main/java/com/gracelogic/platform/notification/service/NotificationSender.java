package com.gracelogic.platform.notification.service;

import java.util.UUID;

public interface NotificationSender {
    boolean send(String source, String destination, String content, UUID notificationMethodId);

    boolean supports(UUID notificationMethodId);
}
