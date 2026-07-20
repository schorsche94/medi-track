package com.schorsche94.medi_track.telegram_bot.utils;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

public class TelegramUtils {

    public static void hideKeyboard(Long chatId, String message, TelegramClient telegramClient) throws TelegramApiException {
        SendMessage sendMessage = SendMessage.builder()
                .text(message)
                .chatId(chatId)
                .build();

        ReplyKeyboardRemove keyboardRemove = new ReplyKeyboardRemove(true);

        sendMessage.setReplyMarkup(keyboardRemove);

        telegramClient.execute(sendMessage);
    }

    public static void sendMessage(Long chatId, String message, TelegramClient telegramClient) throws TelegramApiException {
        SendMessage sendMessage = SendMessage.builder()
                .text(message)
                .chatId(chatId)
                .build();
        sendMessage.setParseMode("HTML");
        telegramClient.execute(sendMessage);
    }
}
