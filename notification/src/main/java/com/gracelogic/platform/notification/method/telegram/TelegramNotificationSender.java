package com.gracelogic.platform.notification.method.telegram;

import com.gracelogic.platform.notification.dto.Content;
import com.gracelogic.platform.notification.dto.NotificationSenderResult;
import com.gracelogic.platform.notification.service.DataConstants;
import com.gracelogic.platform.notification.service.NotificationSender;
import com.gracelogic.platform.property.service.PropertyService;
import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.UUID;

@Service("telegramNotificationSender")
public class TelegramNotificationSender implements NotificationSender {
    @Autowired
    private PropertyService propertyService;

    private TelegramBot telegramBot = null;

    private static Log logger = LogFactory.getLog(TelegramNotificationSender.class);


    @PostConstruct
    private void init(){
        String telegramBotUsername = propertyService.getPropertyValue("notification:telegram_bot_username");
        String telegramBotToken = propertyService.getPropertyValue("notification:telegram_bot_token");
        if (!StringUtils.isEmpty(telegramBotUsername) && !StringUtils.isEmpty(telegramBotToken)) {
            telegramBot = new TelegramBot(telegramBotUsername, telegramBotToken);

            try (TelegramBotsLongPollingApplication botsApplication = new TelegramBotsLongPollingApplication()){
                botsApplication.registerBot(telegramBotToken, telegramBot);
            } catch (TelegramApiException e) {
                logger.error("Failed to initialize telegram bot", e);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public NotificationSenderResult send(String source, String destination, Content content) {
        try {
            if (telegramBot == null) {
                throw new RuntimeException("Telegram bot not initialized");
            }

            telegramBot.sendNotification(content.getBody(), destination);
            return new NotificationSenderResult(true, null);
        } catch (Exception e) {
            return new NotificationSenderResult(false, e.getMessage());
        }
    }

    @Override
    public boolean supports(UUID notificationMethodId) {
        return notificationMethodId != null && notificationMethodId.equals(DataConstants.NotificationMethods.TELEGRAM.getValue());
    }
}
