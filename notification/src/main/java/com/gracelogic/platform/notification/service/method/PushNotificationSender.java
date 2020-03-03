package com.gracelogic.platform.notification.service.method;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gracelogic.platform.notification.dto.NotificationSenderResult;
import com.gracelogic.platform.notification.exception.TransportException;
import com.gracelogic.platform.notification.firebase.protocol.FcmMessage;
import com.gracelogic.platform.notification.firebase.protocol.FcmResponse;
import com.gracelogic.platform.notification.firebase.protocol.PushContentWS;
import com.gracelogic.platform.notification.firebase.utils.HttpUtils;
import com.gracelogic.platform.notification.model.Notification;
import com.gracelogic.platform.notification.service.DataConstants;
import com.gracelogic.platform.notification.service.NotificationSender;
import com.gracelogic.platform.notification.service.transport.NotificationFields;
import com.gracelogic.platform.property.service.PropertyService;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Map;
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
