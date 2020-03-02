package com.gracelogic.platform.notification.service.method;

import com.gracelogic.platform.notification.dto.NotificationSenderResult;
import com.gracelogic.platform.notification.service.DataConstants;
import com.gracelogic.platform.notification.service.NotificationSender;
import com.gracelogic.platform.property.service.PropertyService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Date;
import java.util.Properties;
import java.util.UUID;

@Service
public class EmailNotificationSender implements NotificationSender {
    private static Logger logger = Logger.getLogger(EmailNotificationSender.class);

    @Autowired
    private PropertyService propertyService;

    @Override
    public NotificationSenderResult send(String source, String destination, String content, String preview) {
        logger.info(String.format("Sending e-mail to: %s", destination));

        try {
            Properties p = new Properties();
            p.put("mail.smtp.host", propertyService.getPropertyValue("notification:smtp_host"));
            p.put("mail.smtp.port", propertyService.getPropertyValue("notification:smtp_port"));
            p.put("mail.smtp.auth", propertyService.getPropertyValue("notification:smtp_auth"));
            p.put("mail.smtp.socketFactory.port", propertyService.getPropertyValue("notification:smtp_socketFactory_port"));
            p.put("mail.smtp.socketFactory.class", propertyService.getPropertyValue("notification:smtp_socketFactory_class"));
            p.put("mail.smtp.ssl.enable", propertyService.getPropertyValue("notification:smtp_ssl_enable"));
            Authenticator auth = new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(propertyService.getPropertyValue("notification:smtp_user"), propertyService.getPropertyValue("notification:smtp_password"));
                }
            };
            Session s = Session.getInstance(p, auth);
            javax.mail.Message msg = new MimeMessage(s);
            msg.setFrom(new InternetAddress(source));
            msg.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(destination));
            msg.setSubject(preview);
            msg.setSentDate(new Date());
            MimeBodyPart bodyPart = new MimeBodyPart();
            Multipart body = new MimeMultipart();
            bodyPart.setText(content, "utf-8");
            body.addBodyPart(bodyPart);
            msg.setContent(body);

            Transport.send(msg);
        } catch (Exception e) {
            return new NotificationSenderResult(false, e.getMessage());
        }
        return new NotificationSenderResult(true, null);
    }

    @Override
    public boolean supports(UUID notificationMethodId) {
        return notificationMethodId != null && notificationMethodId.equals(DataConstants.NotificationMethods.EMAIL.getValue());
    }


}
