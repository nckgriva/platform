package com.gracelogic.platform.notification.service.method;

import com.gracelogic.platform.notification.dto.NotificationSenderResult;
import com.gracelogic.platform.notification.service.DataConstants;
import com.gracelogic.platform.notification.service.NotificationSender;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class InternalNotificationSender implements NotificationSender {
    private static Logger logger = Logger.getLogger(InternalNotificationSender.class);

    @Override
    public NotificationSenderResult send(String source, String destination, String content, String preview) {
        //Nothing to do, because INTERNAL method only save notification in DB
        return new NotificationSenderResult(true, null);
    }

    @Override
    public boolean supports(UUID notificationMethodId) {
        return notificationMethodId != null && notificationMethodId.equals(DataConstants.NotificationMethods.INTERNAL.getValue());
    }
}
