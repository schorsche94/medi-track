package com.schorsche94.medi_track.telegram_bot.menu;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.ArrayList;
import java.util.List;

public class TelegramMenu {

    public static void sendMainMenu(Long chatId, TelegramClient telegramClient) throws TelegramApiException {
        SendMessage message = SendMessage.builder()
                .text("Welcome! Choose what you want to do!")
                .chatId(chatId)
                .build();
        var button1 = InlineKeyboardButton.builder().text("Show medication for today").callbackData("show_today_medication_list").build();
        var button2 = InlineKeyboardButton.builder().text("Medication menu").callbackData("menu_medication").build();
        List<InlineKeyboardRow> keyboardRows = List.of(
                new InlineKeyboardRow(button1),
                new InlineKeyboardRow(button2));
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(keyboardRows);
        message.setReplyMarkup(markup);
        telegramClient.execute(message);
    }

    public static void sendMenuMedication(Long chatId, TelegramClient telegramClient) throws TelegramApiException {
        SendMessage sendMessage = SendMessage.builder()
                .text("Medication actions")
                .chatId(chatId)
                .build();

        List<InlineKeyboardRow> rowsInline = new ArrayList<>();

        InlineKeyboardButton button1 = InlineKeyboardButton.builder().text("Add medication").callbackData("add_medication").build();
        InlineKeyboardButton button2 = InlineKeyboardButton.builder().text("Show all medications").callbackData("show_all_medications").build();
        InlineKeyboardRow row1 = new InlineKeyboardRow(button1, button2);

        rowsInline.add(row1);

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup(rowsInline);
        markupInline.setKeyboard(rowsInline);
        sendMessage.setReplyMarkup(markupInline);

        telegramClient.execute(sendMessage);
    }

    public static void sendMenuMedicationForm(Long chatId, TelegramClient telegramClient) throws TelegramApiException {
        SendMessage sendMessage = SendMessage.builder()
                .text("Enter medication form:")
                .chatId(chatId)
                .build();

        List<KeyboardRow> rowsInline = new ArrayList<>();

        KeyboardButton button1 = KeyboardButton.builder().text("TABLET").build();
        KeyboardButton button2 = KeyboardButton.builder().text("CAPSULE").build();
        KeyboardRow row1 = new KeyboardRow(button1, button2);

        KeyboardButton button3 = KeyboardButton.builder().text("DROPS").build();
        KeyboardButton button4 = KeyboardButton.builder().text("SUPPOSITORY").build();
        KeyboardRow row2 = new KeyboardRow(button3, button4);

        KeyboardButton button5 = KeyboardButton.builder().text("SYRUP").build();
        KeyboardButton button6 = KeyboardButton.builder().text("OINTMENT").build();
        KeyboardRow row3 = new KeyboardRow(button5, button6);

        KeyboardButton button7 = KeyboardButton.builder().text("CREAM").build();
        KeyboardButton button8 = KeyboardButton.builder().text("SPRAY").build();
        KeyboardRow row4 = new KeyboardRow(button7, button8);

        KeyboardButton button9 = KeyboardButton.builder().text("GEL").build();
        KeyboardButton button10 = KeyboardButton.builder().text("INJECTION").build();
        KeyboardRow row5 = new KeyboardRow(button9, button10);

        rowsInline.add(row1);
        rowsInline.add(row2);
        rowsInline.add(row3);
        rowsInline.add(row4);
        rowsInline.add(row5);

        ReplyKeyboardMarkup markupInline = new ReplyKeyboardMarkup(rowsInline);
        markupInline.setKeyboard(rowsInline);
        sendMessage.setReplyMarkup(markupInline);

        telegramClient.execute(sendMessage);
    }
}
