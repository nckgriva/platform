package com.gracelogic.platform.notification.method.sms;

import com.gracelogic.platform.notification.dto.Content;
import com.gracelogic.platform.notification.dto.NotificationSenderResult;
import com.gracelogic.platform.notification.service.DataConstants;
import com.gracelogic.platform.notification.service.NotificationSender;
import com.gracelogic.platform.property.service.PropertyService;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.util.UUID;

@Service("smsNotificationSender")
public class SmsNotificationSender implements NotificationSender {
    @Autowired
    private PropertyService propertyService;

    private Log logger = LogFactory.getLog(getClass());

    private final String API_URL = "http://sms.ru/sms/send?api_id=%s&to=%s&text=%s%s";

    public NotificationSenderResult send(String source, String destination, Content content) {
        logger.info("Sending sms to: %s".formatted(destination));

        try {
            CloseableHttpClient httpClient = HttpClients.custom().build();

            String uri = String.format(
                    API_URL,
                    propertyService.getPropertyValue("notification:sms_apikey"),
                    destination,
                    URLEncoder.encode(content.getBody(), "UTF-8"),
                    !StringUtils.isEmpty(source) ? String.format("&from=%s", source) : ""
            );

            HttpGet sendMethod = new HttpGet(uri);
            CloseableHttpResponse result = httpClient.execute(sendMethod);

            HttpEntity entity = result.getEntity();
            String response = EntityUtils.toString(entity);
            EntityUtils.consume(entity);

            boolean success = result.getStatusLine().getStatusCode() == HttpStatus.SC_OK;

            return new NotificationSenderResult(success, success ? null : response);
        } catch (Exception e) {
            return new NotificationSenderResult(false, e.getMessage());
        }
    }

    @Override
    public boolean supports(UUID notificationMethodId) {
        return notificationMethodId != null && notificationMethodId.equals(DataConstants.NotificationMethods.SMS.getValue());
    }

}
