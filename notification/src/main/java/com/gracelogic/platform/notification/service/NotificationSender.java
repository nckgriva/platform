package com.gracelogic.platform.notification.service;

import com.gracelogic.platform.notification.dto.NotificationSenderResult;
import com.gracelogic.platform.notification.exception.TransportException;

import java.util.UUID;

public interface NotificationSender {
    NotificationSenderResult send(String source, String destination, String content, String preview);

    boolean supports(UUID notificationMethodId);
}
