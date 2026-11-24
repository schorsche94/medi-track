package com.schorsche94.medi_track.telegram_bot.service;

import com.schorsche94.medi_track.domain.model.MedicationIntake;
import com.schorsche94.medi_track.service.MedicationIntakeService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.schorsche94.medi_track.telegram_bot.utils.TelegramUtils.sendMessage;

@Service

public class TelegramMedicationIntakeService {

    @Autowired
    private MedicationIntakeService medicationIntakeService;

    @SneakyThrows
    public void prepareAndSendTodayMedicationToTG(Long chatId, TelegramClient telegramClient) {
        var medications = medicationIntakeService.getIntakesForToday(chatId);
        if (medications.isEmpty()) {
            sendMessage(chatId, "You don`t have medications for today.", telegramClient);
        } else {
//            StringBuilder sb = new StringBuilder();
//            sb.append("\uD83D\uDC8A Medication list for today:\n\n");

            prepareMedicationIntakeList(chatId, telegramClient, medications);
        }
    }

    @SneakyThrows
    private static void prepareMedicationIntakeList(Long chatId, TelegramClient telegramClient, List<MedicationIntake> medicationIntakes) {
        for (MedicationIntake m : medicationIntakes) {
            StringBuilder sb = new StringBuilder();
            prepareMedicationIntake(sb, m);
            sendMedicationIntake(chatId, telegramClient, sb, m);
        }
    }

    private static void prepareMedicationIntake(StringBuilder sb, MedicationIntake medicationIntake) {
        sb.append("\uD83D\uDC8A <b>")
                .append(medicationIntake.getMedication().getName())
                .append("</b>\n");

        if (medicationIntake.getMedication().getActiveSubstance() != null && !medicationIntake.getMedication().getActiveSubstance().isEmpty()) {
            sb.append("  Active substance: ")
                    .append(medicationIntake.getMedication().getActiveSubstance())
                    .append("\n");
        }

        if (medicationIntake.getDosage() != null) {
            sb.append("  Dosage: ").append(medicationIntake.getDosage())
                    .append("\n");
        }

        if (medicationIntake.getMedication().getInstructions() != null && !medicationIntake.getMedication().getInstructions().isEmpty()) {
            sb.append("  Medication instructions: ")
                    .append(medicationIntake.getMedication().getInstructions())
                    .append("\n");
        }

        if (medicationIntake.getScheduledAt() != null) {
            sb.append("  Scheduled at: ")
                    .append(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                            .withZone(ZoneOffset.UTC)
                            .format(medicationIntake.getScheduledAt()))
                    .append("\n");
        }

        if (medicationIntake.getStatus() != null) {
            sb.append("  Status: ")
                    .append(medicationIntake.getStatus())
                    .append("\n");
        }

        sb.append("\n");
    }

    private static void sendMedicationIntake(Long chatId, TelegramClient telegramClient, StringBuilder sb, MedicationIntake medication) throws TelegramApiException {
        SendMessage sendMessage = SendMessage.builder()
                .text(sb.toString())
                .chatId(chatId)
                .build();
        sendMessage.setParseMode("HTML");

//        List<InlineKeyboardRow> rowsInline = new ArrayList<>();
//
//        InlineKeyboardButton button1 = InlineKeyboardButton.builder().text("Edit medication").callbackData("edit_medication:" + medication.getId()).build();
//        InlineKeyboardButton button2 = InlineKeyboardButton.builder().text("Delete medication").callbackData("delete_medication:" + medication.getId()).build();
//        InlineKeyboardRow row1 = new InlineKeyboardRow(button1, button2);
//
//        InlineKeyboardButton button3 = InlineKeyboardButton.builder().text("Schedule medication").callbackData("schedule_medication:" + medication.getId()).build();
//
//        InlineKeyboardRow row2 = new InlineKeyboardRow(button3);
//
//        rowsInline.add(row1);
//        rowsInline.add(row2);
//
//        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup(rowsInline);
//        markupInline.setKeyboard(rowsInline);
//        sendMessage.setReplyMarkup(markupInline);

        telegramClient.execute(sendMessage);
    }
}
