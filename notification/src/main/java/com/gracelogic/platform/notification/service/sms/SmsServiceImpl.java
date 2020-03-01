package com.gracelogic.platform.notification.service.sms;

import com.gracelogic.platform.notification.dto.Message;
import com.gracelogic.platform.property.service.PropertyService;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class SmsServiceImpl implements SmsService {
    @Autowired
    private PropertyService propertyService;

    private Logger logger = Logger.getLogger(getClass());

    private final String sendSms = "http://sms.ru/sms/send?api_id=%s&to=%s&text=%s%s";

    public boolean sendSms(Message message) {
        try {
            CloseableHttpClient httpClient = HttpClients.custom().build();

            String uri = String.format(
                    sendSms,
                    propertyService.getPropertyValue("notification:sms_apikey"),
                    message.getTo(),
                    URLEncoder.encode(message.getText(), "UTF-8"),
                    !StringUtils.isEmpty(message.getFrom()) ? String.format("&from=%s", message.getFrom()) : ""
            );

            logger.info("uri :" + uri);
            HttpGet sendMethod = new HttpGet(uri);
            CloseableHttpResponse result = httpClient.execute(sendMethod);

            HttpEntity entity = result.getEntity();
            String response = EntityUtils.toString(entity);
            EntityUtils.consume(entity);

            logger.info(response);
            return result.getStatusLine().getStatusCode() == HttpStatus.SC_OK;
        } catch (UnsupportedEncodingException e) {
            logger.error("", e);
        } catch (IOException e) {
            logger.error("", e);
        }
        return false;
    }

}
