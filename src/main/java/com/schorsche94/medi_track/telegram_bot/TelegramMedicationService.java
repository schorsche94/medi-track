package com.schorsche94.medi_track.telegram_bot;

import com.schorsche94.medi_track.domain.enums.MedicationForm;
import com.schorsche94.medi_track.domain.model.Medicine;
import com.schorsche94.medi_track.telegram_bot.tg_model.Conversation;
import com.schorsche94.medi_track.telegram_bot.tg_model.ConversationState;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;

import static com.schorsche94.medi_track.telegram_bot.TelegramMenu.sendMenuMedicationForm;
import static com.schorsche94.medi_track.telegram_bot.utils.TelegramUtils.hideKeyboard;
import static com.schorsche94.medi_track.telegram_bot.utils.TelegramUtils.sendMessage;

@Service
public class TelegramMedicationService {

    private Conversation conversation;

    public Conversation addMedication(Long chatId, TelegramClient telegramClient) {
        Conversation conversation = new Conversation();
        conversation.setState(ConversationState.WAITING_NAME);


        sendMessage(chatId, "Enter medication name:", telegramClient);
        return conversation;
    }

    public void handleConversation(Long chatId, String text, Conversation conversation, TelegramClient telegramClient) {
        switch (conversation.getState()) {

            case WAITING_NAME -> {
                conversation.setName(text);
                conversation.setState(ConversationState.WAITING_DESCRIPTION);

                sendMessage(chatId, "Enter medication description:", telegramClient);
            }

            case WAITING_DESCRIPTION -> {
                conversation.setDescription(text);
                conversation.setState(ConversationState.WAITING_DOZE);

                sendMessage(chatId, "Enter medication dosage:", telegramClient);
            }

            case WAITING_DOZE -> {
                try {
                    conversation.setDosage(text);
                } catch (NumberFormatException e) {
                    conversation.setState(ConversationState.WAITING_DOZE);
                    sendMessage(chatId, "Please enter a valid number", telegramClient);
                }
                conversation.setState(ConversationState.WAITING_MEDICATION_FORM);

                sendMenuMedicationForm(chatId, telegramClient);
            }

            case WAITING_MEDICATION_FORM -> {
                conversation.setMedicationForm(MedicationForm.valueOf(text));
                conversation.setState(ConversationState.NONE);



                hideKeyboard(chatId, "Medication added!", telegramClient);
            }

            default -> {
                sendMessage(chatId, "Something went wrong", telegramClient);
                conversation.setState(ConversationState.NONE);
            }
        }
    }

    public void prepareAndSendTodayMedicationToTG(List<Medicine> medications, Long chatId, TelegramClient telegramClient) {
        if(medications.isEmpty()) {
            sendMessage(chatId, "You don`t have medications for today.", telegramClient);
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("\uD83D\uDC8A Medication list for today:\n\n");

            prepareMedicationList(medications, sb);

            sendMessage(chatId, sb.toString(), telegramClient);
        }
    }

    public void prepareAndSendMedicationsToTG(List<Medicine> medications, Long chatId, TelegramClient telegramClient) {
        if(medications.isEmpty()) {
            sendMessage(chatId, "You don`t have medications.", telegramClient);
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("\uD83D\uDC8A Medication list:\n\n");

            prepareMedicationList(medications, sb);

            sendMessage(chatId, sb.toString(), telegramClient);
        }
    }

    private static void prepareMedicationList(List<Medicine> medications, StringBuilder sb) {
        for (Medicine m : medications) {
            sb.append("🔹 ")
                    .append(m.getName())
                    .append("\n");

            if (m.getDescription() != null && !m.getDescription().isEmpty()) {
                sb.append("   ")
                        .append(m.getDescription())
                        .append("\n");
            }

            if (m.getDosage() != null) {
                sb.append("  Dosage: ").append(m.getDosage())
                        .append("\n");
            }

            if (m.getMedicationForm() != null) {
                sb.append("  Medication type: ")
                        .append(m.getMedicationForm())
                        .append("\n");
            }

            sb.append("\n");
        }
    }
}
