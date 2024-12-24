package com.gracelogic.platform.notification.method.telegram;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.LinkedList;
import java.util.List;

public class TelegramBot implements LongPollingSingleThreadUpdateConsumer  {
    private String botToken;
    private String botUsername;
    private ReplyKeyboardMarkup keyboard;

    private final TelegramClient telegramClient;

    private static Log logger = LogFactory.getLog(TelegramBot.class);

    @Override
    public void consume(Update update) {
        String command = update.getMessage().getText();
        if (StringUtils.equalsIgnoreCase(command, "/start") || StringUtils.equalsIgnoreCase(command, "/id")) {
            returnChatId(update);
        }
    }

    public TelegramBot(String botToken, String botUsername) {
        this.botToken = botToken;
        this.botUsername = botUsername;
        setKeyboards();

        telegramClient = new OkHttpTelegramClient(botToken);
    }

    private void setKeyboards() {
        KeyboardRow row = new KeyboardRow();
        row.add("id");

        List<KeyboardRow> buttonsList = new LinkedList<>();
        buttonsList.add(row);

        keyboard = new ReplyKeyboardMarkup(buttonsList);
        this.keyboard.setResizeKeyboard(true);
    }

    private void returnChatId(Update update) {
        SendMessage sendMessage = new SendMessage(
                update.getMessage().getChatId().toString(),
                update.getMessage().getChatId().toString());
        sendMessage.setReplyMarkup(this.keyboard);

        try {
            telegramClient.execute(sendMessage);
        } catch (TelegramApiException e) {
            logger.error("Failed to send message", e);
        }
    }

    public void sendNotification(String text, String chatId) throws TelegramApiException {
        SendMessage notificationMessage = new SendMessage(chatId, text);
        notificationMessage.setReplyMarkup(this.keyboard);
        telegramClient.execute(notificationMessage);
    }
}
