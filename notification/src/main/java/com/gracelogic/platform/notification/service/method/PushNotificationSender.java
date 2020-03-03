package com.gracelogic.platform.notification.service.method;

import com.gracelogic.platform.notification.dto.NotificationSenderResult;
import com.gracelogic.platform.notification.firebase.utils.HttpUtils;
import com.gracelogic.platform.notification.service.DataConstants;
import com.gracelogic.platform.notification.service.NotificationSender;
import com.gracelogic.platform.property.service.PropertyService;
import org.apache.http.client.HttpClient;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

public class PushNotificationSender implements NotificationSender {

    @Autowired
    private PropertyService propertyService;

    private static Logger logger = Logger.getLogger(EmailNotificationSender.class);
    private static final String FCM_SERVICE_URL = "https://fcm.googleapis.com/fcm/send";
    private final String authorizationKey;
    private HttpClient httpClient;

    public PushNotificationSender(String authorizationKey) {
        this.authorizationKey = authorizationKey;
        httpClient = HttpUtils.createTrustAllSecuredHttpClient();
    }

    @Override
    public NotificationSenderResult send(String source, String destination, String content, String preview) {
        return null;
    }

    @Override
    public boolean supports(UUID notificationMethodId) {
        return notificationMethodId != null && notificationMethodId.equals(DataConstants.NotificationMethods.PUSH.getValue());
    }

}
