package com.gracelogic.platform.notification.method.telegram;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.LinkedList;
import java.util.List;

public class TelegramBot extends TelegramLongPollingBot {
    private String botToken;
    private String botUsername;
    private ReplyKeyboardMarkup keyboard;

    private static Logger logger = LoggerFactory.getLogger(TelegramBot.class);

    @Override
    public void onUpdateReceived(Update update) {
        String command = update.getMessage().getText();
        if (StringUtils.equalsIgnoreCase(command, "/start") || StringUtils.equalsIgnoreCase(command, "/id")) {
            returnChatId(update);
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

    public TelegramBot(String botToken, String botUsername) {
        this.botToken = botToken;
        this.botUsername = botUsername;
        setKeyboards();
    }

    private void setKeyboards() {
        KeyboardRow row = new KeyboardRow();
        row.add("id");

        List<KeyboardRow> buttonsList = new LinkedList<>();
        buttonsList.add(row);

        keyboard = new ReplyKeyboardMarkup();
        this.keyboard.setKeyboard(buttonsList);
        this.keyboard.setResizeKeyboard(true);
    }

    private void returnChatId(Update update) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getMessage().getChatId());
        sendMessage.setText(update.getMessage().getChatId().toString());
        sendMessage.setReplyMarkup(this.keyboard);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            logger.error("Failed to send message", e);
        }
    }

    public void sendNotification(String text, String chatId) throws TelegramApiException {
        SendMessage notificationMessage = new SendMessage();
        notificationMessage.setText(text);
        notificationMessage.setChatId(chatId);
        notificationMessage.setReplyMarkup(this.keyboard);
        execute(notificationMessage);
    }
}
