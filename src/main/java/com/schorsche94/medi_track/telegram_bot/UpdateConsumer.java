package com.schorsche94.medi_track.telegram_bot;

import com.schorsche94.medi_track.api.service.MedicationService;
import com.schorsche94.medi_track.api.service.model.Medication;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.ArrayList;
import java.util.List;

@Component
public class UpdateConsumer implements LongPollingSingleThreadUpdateConsumer {
    private final TelegramClient telegramClient;

    private String token;

    @Autowired
    private MedicationService medicationService;

    public UpdateConsumer(@Value("${telegram.bot.token}") String token) {
        this.token = token;
        this.telegramClient = new OkHttpTelegramClient(token);
    }

    @SneakyThrows
    @Override
    public void consume(Update update) {
        if (update.hasMessage()) {
            String messageText = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();
            if (messageText.equals("/start")) {
                sendMainMenu(chatId);
            } else if(messageText.equals("/show_today_medication_list") || messageText.equals("show_today_medication_list")) {
               var medications = medicationService.getMedicationList();
                prepareAndSendListToTG(medications, chatId);
            } else if(messageText.equals("/menu_medication")) {
                sendMenuMedication(chatId);
            } else {
                sendMessage(chatId, "Unknown operation!");
            }
        } else if (update.hasCallbackQuery()) {
            handleCallbackQuery(update.getCallbackQuery());
        }
    }

    private void prepareAndSendListToTG(List<Medication> medications, Long chatId) {
        if(medications.isEmpty()) {
            sendMessage(chatId, "You don`t have medications for today.");
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("\uD83D\uDC8A Medication list for today:\n\n");

            for (Medication m : medications) {
                sb.append("🔹 ")
                        .append(m.getName())
                        .append("\n");

                if (m.getDescription() != null && !m.getDescription().isEmpty()) {
                    sb.append("   ")
                            .append(m.getDescription())
                            .append("\n");
                }

                if (m.getDoze() != null) {
                    sb.append("  Doze: ").append(m.getDoze()).append(" ").append(m.getDozeType())
                            .append("\n");
                }

                if (m.getMedicationForm() != null) {
                    sb.append("  Medication type: ")
                            .append(m.getMedicationForm())
                            .append("\n");
                }

                sb.append("\n");
            }

            sendMessage(chatId, sb.toString());
        }
    }

    private InlineKeyboardMarkup createInlineListKeyboard(List<Medication> medications) {
        List<InlineKeyboardRow> rows = new ArrayList<>();

        for (Medication m : medications) {
            InlineKeyboardButton button = InlineKeyboardButton.builder()
                    .text(m.getName() + " - " + m.getDescription() + " - " + m.getDoze() + " - " + m.getDozeType())
                    .callbackData("medication_" + m.getId())
                    .build();
            InlineKeyboardRow row1 = new InlineKeyboardRow(button);
            rows.add(row1);
        }

        return InlineKeyboardMarkup.builder().keyboard(rows).build();
    }

    @SneakyThrows
    private void sendMenuMedication(Long chatId) {
        SendMessage sendMessage = SendMessage.builder()
                .text("Medication keyboard")
                .chatId(chatId)
                .build();

        List<InlineKeyboardRow> rowsInline = new ArrayList<>();

        InlineKeyboardButton button1 =  InlineKeyboardButton.builder().text("Add medication").callbackData("add_medication").build();
        InlineKeyboardButton button2 =  InlineKeyboardButton.builder().text("Edit medication").callbackData("edit_medication").build();
        InlineKeyboardRow row1 = new InlineKeyboardRow(button1, button2);

        InlineKeyboardButton button3 =  InlineKeyboardButton.builder().text("Delete medication").callbackData("delete_medication").build();
        InlineKeyboardButton button4 =  InlineKeyboardButton.builder().text("Show all medications").callbackData("show_all_medications").build();
        InlineKeyboardRow row2 = new InlineKeyboardRow(button3, button4);

        rowsInline.add(row1);
        rowsInline.add(row2);

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup(rowsInline);
        markupInline.setKeyboard(rowsInline);
        sendMessage.setReplyMarkup(markupInline);

        telegramClient.execute(sendMessage);
    }

    private void handleCallbackQuery(CallbackQuery callbackQuery) {
        var data = callbackQuery.getData();
        var chatId = callbackQuery.getFrom().getId();
        var user = callbackQuery.getFrom();
        switch (data) {
            case "show_today_medication_list" -> {
                var medications = medicationService.getMedicationList();
                prepareAndSendListToTG(medications, chatId);
            }
            case "menu_medication" -> sendMenuMedication(chatId);
            default -> sendMessage(chatId, "Unknown operation!");
        }
    }

    @SneakyThrows
    private void sendMessage(Long chatId, String message) {
        SendMessage sendMessage = SendMessage.builder()
                .text(message)
                .chatId(chatId)
                .build();
        telegramClient.execute(sendMessage);
    }

    @SneakyThrows
    private void sendMainMenu(Long chatId) {
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
}
