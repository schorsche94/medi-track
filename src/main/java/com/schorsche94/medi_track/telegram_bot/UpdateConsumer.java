package com.schorsche94.medi_track.telegram_bot;

import com.schorsche94.medi_track.service.MedicationService;
import com.schorsche94.medi_track.telegram_bot.tg_model.Conversation;
import com.schorsche94.medi_track.telegram_bot.tg_model.ConversationState;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.schorsche94.medi_track.telegram_bot.TelegramMenu.sendMainMenu;
import static com.schorsche94.medi_track.telegram_bot.TelegramMenu.sendMenuMedication;
import static com.schorsche94.medi_track.telegram_bot.utils.TelegramUtils.sendMessage;

@Component
public class UpdateConsumer implements LongPollingSingleThreadUpdateConsumer {

    @Autowired
    private TelegramMedicationService telegramMedicationService;

    private Map<Long, Conversation> conversations = new ConcurrentHashMap<>();

    @Autowired
    private TelegramClient telegramClient;

    @Autowired
    private MedicationService medicationService;

    @SneakyThrows
    @Override
    public void consume(Update update) {
        if (update.hasMessage()) {
            handleMessage(update);
        } else if (update.hasCallbackQuery()) {
            handleCallbackQuery(update.getCallbackQuery());
        }
    }

    private void handleMessage(Update update) {
        String messageText = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();

        Conversation conversation = conversations.get(chatId);

        if (conversation != null && conversation.getState() != ConversationState.NONE) {
            telegramMedicationService.handleConversation(chatId, messageText, conversation, telegramClient);
            return;
        }

        if (messageText.equals("/start")) {
            sendMainMenu(chatId, telegramClient);
        } else if(messageText.equals("/show_today_medication_list") || messageText.equals("show_today_medication_list")) {
           var medications = medicationService.getMedicationList();
            telegramMedicationService.prepareAndSendTodayMedicationToTG(medications, chatId, telegramClient);
        } else if(messageText.equals("/menu_medication")) {
            sendMenuMedication(chatId, telegramClient);
        } else {
            sendMessage(chatId, "Unknown operation!", telegramClient);
        }
    }

    private void handleCallbackQuery(CallbackQuery callbackQuery) {
        var data = callbackQuery.getData();
        var chatId = callbackQuery.getFrom().getId();
        switch (data) {
            case "show_today_medication_list" -> {
                var medications = medicationService.getMedicationList();
                telegramMedicationService.prepareAndSendTodayMedicationToTG(medications, chatId, telegramClient);
            }
            case "menu_medication" -> sendMenuMedication(chatId, telegramClient);
            case "add_medication" -> {
                var conversation = telegramMedicationService.addMedication(chatId, telegramClient);
                conversations.put(chatId, conversation);
            }
            case "show_all_medications" -> {
                var medications = medicationService.getMedicationList();
                telegramMedicationService.prepareAndSendMedicationsToTG(medications, chatId, telegramClient);
            }
            default -> sendMessage(chatId, "Unknown operation!", telegramClient);
        }
    }

}
