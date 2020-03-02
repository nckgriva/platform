package com.gracelogic.platform.notification.service;

import com.gracelogic.platform.notification.model.Notification;

import java.util.UUID;
import java.util.concurrent.Future;

public interface NotificationService {
    Future<Notification> send(UUID notificationMethodId, String source, String destination, String content, String preview, Integer priority);

    Notification saveNotification(Notification notification);
}
