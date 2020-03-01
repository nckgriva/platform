package com.gracelogic.platform.notification.service;

import com.gracelogic.platform.db.exception.ObjectNotFoundException;
import com.gracelogic.platform.notification.dto.NotificationDTO;
import com.gracelogic.platform.notification.model.Notification;

public interface NotificationService extends NotificationSender {
    Notification saveNotification(NotificationDTO dto) throws ObjectNotFoundException;
}
