package com.schorsche94.medi_track.telegram_bot.menu;

import com.schorsche94.medi_track.domain.model.MedicationForm;
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

    public static void sendMenuMedicationForm(Long chatId, TelegramClient telegramClient, List<MedicationForm> forms) throws TelegramApiException {
        SendMessage sendMessage = SendMessage.builder()
                .text("Choose medication form:")
                .chatId(chatId)
                .build();

        List<KeyboardRow> rowsInline = new ArrayList<>();
        for (int i = 0; i < forms.size(); i += 2) {
            KeyboardRow row = new KeyboardRow();
            row.add(KeyboardButton.builder().text(forms.get(i).getCode()).build());
            if (i + 1 < forms.size()) {
                row.add(KeyboardButton.builder().text(forms.get(i + 1).getCode()).build());
            }
            rowsInline.add(row);
        }

        ReplyKeyboardMarkup markupInline = new ReplyKeyboardMarkup(rowsInline);
        markupInline.setKeyboard(rowsInline);
        sendMessage.setReplyMarkup(markupInline);

        telegramClient.execute(sendMessage);
    }
}
