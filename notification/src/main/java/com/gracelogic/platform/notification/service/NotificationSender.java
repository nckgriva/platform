package com.gracelogic.platform.notification.service;

import com.gracelogic.platform.notification.dto.Content;
import com.gracelogic.platform.notification.dto.NotificationSenderResult;

import java.util.UUID;

public interface NotificationSender {
    NotificationSenderResult send(String source, String destination, Content content);

    boolean supports(UUID notificationMethodId);
}
