package com.schorsche94.medi_track.telegram_bot.service;

import com.schorsche94.medi_track.domain.enums.MedicationForm;
import com.schorsche94.medi_track.domain.model.Medication;
import com.schorsche94.medi_track.service.MedicationService;
import com.schorsche94.medi_track.telegram_bot.mapper.TelegramMedicationMapper;
import com.schorsche94.medi_track.telegram_bot.model.Conversation;
import com.schorsche94.medi_track.telegram_bot.model.ConversationState;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.ArrayList;
import java.util.List;

import static com.schorsche94.medi_track.telegram_bot.menu.TelegramMenu.sendMenuMedicationForm;
import static com.schorsche94.medi_track.telegram_bot.utils.TelegramUtils.hideKeyboard;
import static com.schorsche94.medi_track.telegram_bot.utils.TelegramUtils.sendMessage;

@Service
public class TelegramMedicationService {

    private Conversation conversation;

    @Autowired
    private MedicationService medicationService;

    @Autowired
    private TelegramMedicationMapper mapper;

    @SneakyThrows
    public Conversation addMedication(Long chatId, TelegramClient telegramClient) {
        Conversation conversation = new Conversation();
        conversation.setState(ConversationState.WAITING_NAME);

        sendMessage(chatId, "Enter medication name:", telegramClient);
        return conversation;
    }

    @SneakyThrows
    public void handleConversation(Long chatId, String text, Conversation conversation, TelegramClient telegramClient) {
        switch (conversation.getState()) {

            case WAITING_NAME -> {
                conversation.setName(text);
                conversation.setState(ConversationState.WAITING_ACTIVE_SUBSTANCE);

                sendMessage(chatId, "Enter medication active substance:", telegramClient);
            }

            case WAITING_ACTIVE_SUBSTANCE -> {
                conversation.setActiveSubstance(text);
                conversation.setState(ConversationState.WAITING_DOSAGE);

                sendMessage(chatId, "Enter medication dosage:", telegramClient);
            }

            case WAITING_DOSAGE -> {
                try {
                    conversation.setDosage(text);
                } catch (NumberFormatException e) {
                    conversation.setState(ConversationState.WAITING_DOSAGE);
                    sendMessage(chatId, "Please enter a valid number", telegramClient);
                }
                conversation.setState(ConversationState.WAITING_FORM);

                sendMenuMedicationForm(chatId, telegramClient);
            }

            case WAITING_FORM -> {
                conversation.setForm(MedicationForm.valueOf(text));
                conversation.setState(ConversationState.WAITING_INSTRUCTIONS);

                hideKeyboard(chatId, "Enter medication instructions:", telegramClient);
            }

            case WAITING_INSTRUCTIONS -> {
                conversation.setInstructions(text);
                conversation.setState(ConversationState.NONE);

                var medication = medicationService.createMedication(mapper.toModel(conversation), chatId);

                StringBuilder sb = new StringBuilder();
                sb.append("\uD83D\uDC8A Medication added:\n\n");

                prepareMedication(sb, medication);

                sendMedication(chatId, telegramClient, sb);
            }

            default -> {
                sendMessage(chatId, "Something went wrong", telegramClient);
                conversation.setState(ConversationState.NONE);
            }
        }
    }

    private static void sendMedication(Long chatId, TelegramClient telegramClient, StringBuilder sb) throws TelegramApiException {
        SendMessage sendMessage = SendMessage.builder()
                .text(sb.toString())
                .chatId(chatId)
                .build();
        sendMessage.setParseMode("HTML");

        List<InlineKeyboardRow> rowsInline = new ArrayList<>();

        InlineKeyboardButton button1 = InlineKeyboardButton.builder().text("Edit medication").callbackData("edit_medication").build();
        InlineKeyboardButton button2 = InlineKeyboardButton.builder().text("Delete medication").callbackData("delete_medication").build();
        InlineKeyboardRow row1 = new InlineKeyboardRow(button1, button2);

        InlineKeyboardButton button3 = InlineKeyboardButton.builder().text("Schedule medication").callbackData("schedule_medication").build();

        InlineKeyboardRow row2 = new InlineKeyboardRow(button3);

        rowsInline.add(row1);
        rowsInline.add(row2);

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup(rowsInline);
        markupInline.setKeyboard(rowsInline);
        sendMessage.setReplyMarkup(markupInline);

        telegramClient.execute(sendMessage);
    }

    @SneakyThrows
    public void prepareAndSendTodayMedicationToTG(Long chatId, TelegramClient telegramClient) {
        var medications = medicationService.getMedicationsForToday(chatId);
        if (medications.isEmpty()) {
            sendMessage(chatId, "You don`t have medications for today.", telegramClient);
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("\uD83D\uDC8A Medication list for today:\n\n");

            prepareMedicationList(chatId, telegramClient, medications);
        }
    }

    @SneakyThrows
    public void prepareAndSendMedicationsToTG(Long chatId, TelegramClient telegramClient) {
        var medications = medicationService.getMedications(chatId);
        if (medications.isEmpty()) {
            sendMessage(chatId, "You don`t have medications.", telegramClient);
        } else {
//            StringBuilder sb = new StringBuilder();
//            sb.append("\uD83D\uDC8A Medication list:\n\n");

            prepareMedicationList(chatId, telegramClient, medications);
        }
    }

    @SneakyThrows
    private static void prepareMedicationList(Long chatId, TelegramClient telegramClient, List<Medication> medications) {
        for (Medication m : medications) {
            StringBuilder sb = new StringBuilder();
            prepareMedication(sb, m);
            sendMedication(chatId, telegramClient, sb);
        }
    }

    private static void prepareMedication(StringBuilder sb, Medication medication) {
        sb.append("🔹 <b>")
                .append(medication.getName())
                .append("</b>\n");

        if (medication.getActiveSubstance() != null && !medication.getActiveSubstance().isEmpty()) {
            sb.append("  Active substance: ")
                    .append(medication.getActiveSubstance())
                    .append("\n");
        }

        if (medication.getDosage() != null) {
            sb.append("  Dosage: ").append(medication.getDosage())
                    .append("\n");
        }

        if (medication.getForm() != null) {
            sb.append("  Medication form: ")
                    .append(medication.getForm())
                    .append("\n");
        }

        if (medication.getInstructions() != null && !medication.getInstructions().isEmpty()) {
            sb.append("  Medication instructions: ")
                    .append(medication.getInstructions())
                    .append("\n");
        }

        sb.append("\n");
    }
}
