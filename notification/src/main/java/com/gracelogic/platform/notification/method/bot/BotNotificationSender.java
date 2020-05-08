package com.gracelogic.platform.notification.method.bot;

import com.gracelogic.platform.notification.bots.telegram.TelegramNotificationBot;
import com.gracelogic.platform.notification.dto.Content;
import com.gracelogic.platform.notification.dto.NotificationSenderResult;
import com.gracelogic.platform.notification.service.NotificationSender;
import com.gracelogic.platform.property.service.PropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.PostConstruct;
import java.util.UUID;

@Service
public class BotNotificationSender implements NotificationSender {
    @Autowired
    private PropertyService propertyService;

    private TelegramNotificationBot telegramNotificationBot;

    @PostConstruct
    private void initBots(){
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();

        String telegramUsername = propertyService.getPropertyValue("notification:telegram_bot_username");
        String telegramToken = propertyService.getPropertyValue("notification:telegram_bot_token");
        telegramNotificationBot = new TelegramNotificationBot(telegramUsername, telegramToken);
        try {
            telegramBotsApi.registerBot(telegramNotificationBot);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }


    }

    @Override
    public NotificationSenderResult send(String source, String destination, Content content) {
        return null;
    }

    @Override
    public boolean supports(UUID notificationMethodId) {
        return false;
    }
}
