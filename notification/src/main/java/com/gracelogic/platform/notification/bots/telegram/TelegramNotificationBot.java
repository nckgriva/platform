package com.gracelogic.platform.notification.bots.telegram;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class TelegramNotificationBot extends TelegramLongPollingBot {
    private String botToken;
    private String botUsername;

    @Override
    public void onUpdateReceived(Update update) {
        System.out.println(update.getMessage().getText());
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(update.getMessage().getText());
        sendMessage.setChatId(update.getMessage().getChatId());

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    public TelegramNotificationBot(String botToken, String botUsername) {
        this.botToken = botToken;
        this.botUsername = botUsername;
    }
}
