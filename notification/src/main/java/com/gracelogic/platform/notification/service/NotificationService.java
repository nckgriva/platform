package com.gracelogic.platform.notification.service;

import com.gracelogic.platform.db.dto.EntityListResponse;
import com.gracelogic.platform.notification.dto.NotificationDTO;
import com.gracelogic.platform.notification.model.Notification;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Future;

public interface NotificationService {
    Future<Notification> send(UUID notificationMethodId, String source, String destination, String content, String preview, Integer priority);

    Notification saveNotification(Notification notification);

    EntityListResponse<NotificationDTO> getNotificationsPaged(String name, String notificationMethodId, String notificationStateId, boolean enrich,
                                                              Integer count, Integer page, Integer start, String sortField, String sortDir);
}
