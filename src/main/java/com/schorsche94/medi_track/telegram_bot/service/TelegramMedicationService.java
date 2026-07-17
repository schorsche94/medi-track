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
import org.telegram.telegrambots.meta.generics.TelegramClient;

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

                medicationService.createMedication(mapper.toModel(conversation), chatId);

                sendMessage(chatId, "Medication added!", telegramClient);
            }

            default -> {
                sendMessage(chatId, "Something went wrong", telegramClient);
                conversation.setState(ConversationState.NONE);
            }
        }
    }

    @SneakyThrows
    public void prepareAndSendTodayMedicationToTG(Long chatId, TelegramClient telegramClient) {
        var medications = medicationService.getMedicationsForToday(chatId);
        if(medications.isEmpty()) {
            sendMessage(chatId, "You don`t have medications for today.", telegramClient);
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("\uD83D\uDC8A Medication list for today:\n\n");

            prepareMedicationList(medications, sb);

            sendMessage(chatId, sb.toString(), telegramClient);
        }
    }

    @SneakyThrows
    public void prepareAndSendMedicationsToTG(Long chatId, TelegramClient telegramClient) {
        var medications = medicationService.getMedications(chatId);
        if(medications.isEmpty()) {
            sendMessage(chatId, "You don`t have medications.", telegramClient);
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("\uD83D\uDC8A Medication list:\n\n");

            prepareMedicationList(medications, sb);

            sendMessage(chatId, sb.toString(), telegramClient);
        }
    }

    private static void prepareMedicationList(List<Medication> medications, StringBuilder sb) {
        for (Medication m : medications) {
            sb.append("🔹 ")
                    .append(m.getName())
                    .append("\n");

            if (m.getActiveSubstance() != null && !m.getActiveSubstance().isEmpty()) {
                sb.append("   ")
                        .append(m.getActiveSubstance())
                        .append("\n");
            }


            if (m.getDosage() != null) {
                sb.append("  Dosage: ").append(m.getDosage())
                        .append("\n");
            }

            if (m.getForm() != null) {
                sb.append("  Medication type: ")
                        .append(m.getForm())
                        .append("\n");
            }

            if (m.getInstructions() != null && !m.getInstructions().isEmpty()) {
                sb.append("   ")
                        .append(m.getInstructions())
                        .append("\n");
            }

            sb.append("\n");
        }
    }
}
