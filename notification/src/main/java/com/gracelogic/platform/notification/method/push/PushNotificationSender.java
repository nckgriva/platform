package com.gracelogic.platform.notification.method.push;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gracelogic.platform.notification.dto.Content;
import com.gracelogic.platform.notification.dto.NotificationSenderResult;
import com.gracelogic.platform.notification.service.HttpUtils;
import com.gracelogic.platform.notification.service.DataConstants;
import com.gracelogic.platform.notification.service.NotificationSender;
import com.gracelogic.platform.property.service.PropertyService;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.UUID;

@Service("pushNotificationSender")
public class PushNotificationSender implements NotificationSender {
    @Autowired
    private PropertyService propertyService;

    private static final String FCM_SERVICE_URL = "https://fcm.googleapis.com/fcm/send";

    private static Log logger = LogFactory.getLog(PushNotificationSender.class);

    @Override
    public NotificationSenderResult send(String source, String destination, Content content) {
        HttpPost post = new HttpPost(FCM_SERVICE_URL);
        post.addHeader("Authorization", "key=" + propertyService.getPropertyValue("notification:firebase_auth_key"));
        try {
            ObjectMapper mapper = new ObjectMapper();
            FcmMessage fcmMessage = createFcmMessage(destination, content);
            String json = mapper.writeValueAsString(fcmMessage);

            StringEntity entity = new StringEntity(json, "UTF-8");
            entity.setContentType("application/json");
            post.setEntity(entity);
            HttpResponse httpResponse = HttpUtils.createTrustAllSecuredHttpClient().execute(post);

            String responseJson = EntityUtils.toString(httpResponse.getEntity());
            logger.info("Response received: %s; content: %s".formatted(httpResponse.getStatusLine(), responseJson));

            FcmResponse response = mapper.readValue(responseJson, FcmResponse.class);
            if (response != null && response.getFailure() > 0) {
                return new NotificationSenderResult(false, responseJson);
            }
        } catch (IOException ex) {
            return new NotificationSenderResult(false, ex.getMessage());
        }

        return new NotificationSenderResult(true, null);
    }

    private FcmMessage createFcmMessage(String destination, Content content) {
        FcmNotification fcmNotification = new FcmNotification();
        fcmNotification.setTitle(content.getTitle());
        fcmNotification.setBody(content.getBody());
        fcmNotification.setCategory(content.getFields().get("category"));
        fcmNotification.setBadge(content.getFields().get("badge"));
        fcmNotification.setSound(content.getFields().get("sound"));
        fcmNotification.setClickAction(content.getFields().get("clickAction"));

        FcmMessage request = FcmMessage.to(destination);
        request.setNotification(fcmNotification);

        if (content.getFields() != null) {
            for (String key : content.getFields().keySet()) {
                request.getData().put(key, content.getFields().get(key));
            }
        }

        request.setTimeToLive(0L);
        return request;
    }

    @Override
    public boolean supports(UUID notificationMethodId) {
        return notificationMethodId != null && notificationMethodId.equals(DataConstants.NotificationMethods.PUSH.getValue());
    }

}
