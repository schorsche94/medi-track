package com.schorsche94.medi_track.telegram_bot.service;

import com.schorsche94.medi_track.domain.model.Medication;
import com.schorsche94.medi_track.service.MedicationCategoryService;
import com.schorsche94.medi_track.service.MedicationFormService;
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

import static com.schorsche94.medi_track.telegram_bot.menu.TelegramMenu.sendMenuMedicationCategory;
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

    @Autowired
    private MedicationFormService medicationFormService;

    @Autowired
    private MedicationCategoryService medicationCategoryService;

    @SneakyThrows
    public Conversation addMedication(Long chatId, TelegramClient telegramClient) {
        Conversation conversation = new Conversation();
        conversation.setState(ConversationState.WAITING_NAME);

        sendMessage(chatId, "Enter medication name:", telegramClient);
        return conversation;
    }

    @SneakyThrows
    public void handleConversation(Long chatId, String text, Conversation conversation, TelegramClient telegramClient) {
        String oldValue = "";
        switch (conversation.getState()) {

            case WAITING_NAME -> {
                if (!text.equals("-")) {
                    conversation.setName(text);
                }
                conversation.setState(ConversationState.WAITING_ACTIVE_SUBSTANCE);

                if (conversation.getActiveSubstance() != null && !conversation.getActiveSubstance().isBlank()) {
                    oldValue = " (current: " + conversation.getActiveSubstance() + ")";
                }
                sendMessage(chatId, "Enter medication active substance:" + oldValue, telegramClient);
            }

            case WAITING_ACTIVE_SUBSTANCE -> {
                if (!text.equals("-")) {
                    conversation.setActiveSubstance(text);
                }
                conversation.setState(ConversationState.WAITING_DOSAGE);

                if (conversation.getDosage() != null && !conversation.getDosage().isBlank()) {
                    oldValue = " (current: " + conversation.getDosage() + ")";
                }

                sendMessage(chatId, "Enter medication dosage:" + oldValue, telegramClient);
            }

            case WAITING_DOSAGE -> {
                try {
                    if(!text.equals("-")) {
                        conversation.setDosage(text);
                    }
                } catch (NumberFormatException e) {
                    conversation.setState(ConversationState.WAITING_DOSAGE);
                    sendMessage(chatId, "Please enter a valid number", telegramClient);
                }
                conversation.setState(ConversationState.WAITING_FORM);

                if (conversation.getForm() != null) {
                    oldValue = " (current: " + conversation.getForm().getName() + ")";
                }

                sendMenuMedicationForm(chatId, telegramClient, medicationFormService.getForms(), oldValue);
            }

            case WAITING_FORM -> {
                if(!text.equals("-")) {
                    var form = medicationFormService.getFormByCode(text);
                    if (conversation.getForm() != null) {
                        oldValue = " (current: " + conversation.getForm().getName() + ")";
                    }
                    if (form == null) {
                        sendMenuMedicationForm(chatId, telegramClient, medicationFormService.getForms(),oldValue);
                        return;
                    }
                    conversation.setForm(form);
                }
                conversation.setState(ConversationState.WAITING_CATEGORY);

                if (conversation.getCategory() != null) {
                    oldValue = " (current: " + conversation.getCategory().getName() + ")";
                }

                sendMenuMedicationCategory(chatId, telegramClient, medicationCategoryService.getCategories(), oldValue);
            }

            case WAITING_CATEGORY -> {
                if(!text.equals("-")) {
                    var form = medicationCategoryService.getCategoryByCode(text);
                    if (conversation.getCategory() != null) {
                        oldValue = " (current: " + conversation.getCategory().getName() + ")";
                    }
                    if (form == null) {
                        sendMenuMedicationCategory(chatId, telegramClient, medicationCategoryService.getCategories(), oldValue);
                        return;
                    }
                    conversation.setCategory(form);
                }
                conversation.setState(ConversationState.WAITING_INSTRUCTIONS);
                if (conversation.getInstructions() != null) {
                    oldValue = " (current: " + conversation.getInstructions() + ")";
                }

                hideKeyboard(chatId, "Enter medication instructions:" + oldValue, telegramClient);
            }

            case WAITING_INSTRUCTIONS -> {
                if(!text.equals("-")) {
                    conversation.setInstructions(text);
                }
                conversation.setState(ConversationState.NONE);
                Medication medication;
                if (conversation.getId() == null) {
                    medication = medicationService.createMedication(mapper.toModel(conversation), chatId);
                } else {
                    medication = medicationService.updateMedication(mapper.toModel(conversation), chatId);
                }


                StringBuilder sb = new StringBuilder();
                sb.append(conversation.getId() == null
                        ? "\uD83D\uDC8A Medication added:\n\n"
                        : "\uD83D\uDC8A Medication updated:\n\n");

                prepareMedication(sb, medication);

                sendMedication(chatId, telegramClient, sb, medication);
            }

            default -> {
                sendMessage(chatId, "Something went wrong", telegramClient);
                conversation.setState(ConversationState.NONE);
            }
        }
    }

    private static void sendMedication(Long chatId, TelegramClient telegramClient, StringBuilder sb, Medication medication) throws TelegramApiException {
        SendMessage sendMessage = SendMessage.builder()
                .text(sb.toString())
                .chatId(chatId)
                .build();
        sendMessage.setParseMode("HTML");

        List<InlineKeyboardRow> rowsInline = new ArrayList<>();

        InlineKeyboardButton button1 = InlineKeyboardButton.builder().text("Edit medication").callbackData("edit_medication:" + medication.getId()).build();
        InlineKeyboardButton button2 = InlineKeyboardButton.builder().text("Delete medication").callbackData("delete_medication:" + medication.getId()).build();
        InlineKeyboardRow row1 = new InlineKeyboardRow(button1, button2);

        InlineKeyboardButton button3 = InlineKeyboardButton.builder().text("Schedule medication").callbackData("schedule_medication:" + medication.getId()).build();

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
            sendMedication(chatId, telegramClient, sb, m);
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
                    .append(medication.getForm().getName())
                    .append("\n");
        }

        if (medication.getCategory() != null) {
            sb.append("  Category: ")
                    .append(medication.getCategory().getName())
                    .append("\n");
        }

        if (medication.getDescription() != null && !medication.getDescription().isBlank()) {
            sb.append("  Purpose: ")
                    .append(medication.getDescription())
                    .append("\n");
        }

        if (medication.getInstructions() != null && !medication.getInstructions().isEmpty()) {
            sb.append("  Medication instructions: ")
                    .append(medication.getInstructions())
                    .append("\n");
        }

        sb.append("\n");
    }

    @SneakyThrows
    public void deleteMedication(Long chatId, Long medicationId, TelegramClient telegramClient) {
        try {
            medicationService.deleteMedication(String.valueOf(medicationId), chatId);
            sendMessage(chatId, "Medication was successfully deleted!", telegramClient);
        } catch (Exception e) {
            sendMessage(chatId, "Medication was not deleted! " + e.getMessage(), telegramClient);
        }
    }

    @SneakyThrows
    public Conversation editMedication(Long chatId, Long medicationId, TelegramClient telegramClient) {
        var oldMedication = medicationService.getMedication(String.valueOf(medicationId), chatId);
        if(oldMedication == null) {
            throw new Exception("Medication was not found!");
        }
        Conversation conversation = mapper.toConversation(oldMedication);
        conversation.setState(ConversationState.WAITING_NAME);

        sendMessage(chatId, "Enter medication name (send - to keep current): (" + conversation.getName() + ")", telegramClient);
        return conversation;
    }
}
