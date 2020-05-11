package com.gracelogic.platform.notification.bots.telegram;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TelegramNotificationBot extends TelegramLongPollingBot {
    private String botToken;
    private String botUsername;

    private ReplyKeyboardMarkup getMyIdKeyboard;

    @Override
    public void onUpdateReceived(Update update) {
        String command = update.getMessage().getText();
        if (command.equals("/start") || command.equals("Get my ID")) {
            returnChatId(update);
        } else if(command.equals("/testnotification")) {
            try {
                sendNotification("Ваш код для смены пароля: 06854", 776942821L);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
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
        setKeyboards();
    }

    private void setKeyboards(){
        KeyboardRow row = new KeyboardRow(); // одна строка с кнопками
        row.add("Get my ID");

        List<KeyboardRow> buttonsList = new LinkedList<>(); // полотенце из строк с кнопками
        buttonsList.add(row);

        getMyIdKeyboard = new ReplyKeyboardMarkup();
        this.getMyIdKeyboard.setKeyboard(buttonsList); // сама клавиатура
        this.getMyIdKeyboard.setResizeKeyboard(true);
    }

    private void returnChatId(Update update){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getMessage().getChatId());
        sendMessage.setText("Ваш идентификатор: " + update.getMessage().getChatId().toString() + "\nВведите его в приложении для подписки на уведомления");
        sendMessage.setReplyMarkup(this.getMyIdKeyboard);
        try {
            send(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void send(SendMessage message) throws TelegramApiException {
        execute(message);
    }

    public void sendNotification(String notificationText, Long chatId) throws TelegramApiException {
        sendNotification(notificationText, chatId.toString());
    }

    public void sendNotification(String notificationText, String chatId) throws TelegramApiException {
        SendMessage notificationMessage = new SendMessage();
        notificationMessage.setText(notificationText);
        notificationMessage.setChatId(chatId);
        notificationMessage.setReplyMarkup(this.getMyIdKeyboard);
        send(notificationMessage);
    }
}
