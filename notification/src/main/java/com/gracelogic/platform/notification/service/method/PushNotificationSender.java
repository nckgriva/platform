package com.gracelogic.platform.notification.service.method;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gracelogic.platform.notification.dto.NotificationSenderResult;
import com.gracelogic.platform.notification.exception.TransportException;
import com.gracelogic.platform.notification.firebase.FcmMessage;
import com.gracelogic.platform.notification.firebase.FcmNotification;
import com.gracelogic.platform.notification.firebase.FcmResponse;
import com.gracelogic.platform.notification.firebase.utils.HttpUtils;
import com.gracelogic.platform.notification.service.DataConstants;
import com.gracelogic.platform.notification.service.NotificationSender;
import com.gracelogic.platform.property.service.PropertyService;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
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
        HttpPost post = new HttpPost(FCM_SERVICE_URL);
        post.addHeader("Authorization", "key=" + propertyService.getPropertyValue("notification:authkey"));

        try {
            ObjectMapper mapper = new ObjectMapper();
            FcmMessage fcmMessage = createFcmMessage(source, destination, content, preview);
            String json = mapper.writeValueAsString(fcmMessage);

            StringEntity entity = new StringEntity(json, "UTF-8");
            entity.setContentType("application/json");
            post.setEntity(entity);
            HttpResponse httpResponse = httpClient.execute(post);

            String responseJson = EntityUtils.toString(httpResponse.getEntity());
            logger.info("Response received: " + httpResponse.getStatusLine() + "; content: " + responseJson);

            FcmResponse response = mapper.readValue(responseJson, FcmResponse.class);
            String error = response.getResults().iterator().next().getError();
            if (error != null) {
                throw new TransportException(error);
            }
        } catch (Exception ex) {
            return new NotificationSenderResult(false, ex.getMessage());
        }

        return new NotificationSenderResult(true, null);
    }

    private FcmMessage createFcmMessage(String source, String destination, String content, String preview) {
        FcmNotification fcmNotification = new FcmNotification();
        fcmNotification.setTitle(preview);
        fcmNotification.setBody(content); // or request.data("content", content);

        FcmMessage request = FcmMessage.to(destination);
        request.setNotification(fcmNotification);
        request.data("source", source);
        return request;
    }

    @Override
    public boolean supports(UUID notificationMethodId) {
        return notificationMethodId != null && notificationMethodId.equals(DataConstants.NotificationMethods.PUSH.getValue());
    }

}
