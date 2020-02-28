package com.gracelogic.platform.notification.service;

import java.util.UUID;

public interface NotificationSender {
    boolean send(String source, String destination, String content);

    boolean supports(UUID notificationMethodId);
}
