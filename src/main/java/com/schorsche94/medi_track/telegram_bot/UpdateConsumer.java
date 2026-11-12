package com.schorsche94.medi_track.telegram_bot;

import com.schorsche94.medi_track.telegram_bot.model.Conversation;
import com.schorsche94.medi_track.telegram_bot.model.ConversationState;
import com.schorsche94.medi_track.telegram_bot.service.TelegramMedicationService;
import com.schorsche94.medi_track.telegram_bot.service.TelegramUserService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.schorsche94.medi_track.telegram_bot.menu.TelegramMenu.sendMainMenu;
import static com.schorsche94.medi_track.telegram_bot.menu.TelegramMenu.sendMenuMedication;
import static com.schorsche94.medi_track.telegram_bot.utils.TelegramUtils.sendMessage;

@Component
public class UpdateConsumer implements LongPollingSingleThreadUpdateConsumer {

    @Autowired
    private TelegramMedicationService telegramMedicationService;

    @Autowired
    private TelegramUserService telegramUserService;

    private Map<Long, Conversation> conversations = new ConcurrentHashMap<>();

    @Autowired
    private TelegramClient telegramClient;

    @SneakyThrows
    @Override
    public void consume(Update update) {
        if (update.hasMessage()) {
            handleMessage(update);
        } else if (update.hasCallbackQuery()) {
            handleCallbackQuery(update.getCallbackQuery());
        }
    }

    @SneakyThrows
    private void handleMessage(Update update) {
        String messageText = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();

        Conversation conversation = conversations.get(chatId);

        if (conversation != null && conversation.getState() != ConversationState.NONE) {
            telegramMedicationService.handleConversation(chatId, messageText, conversation, telegramClient);
            return;
        }

        if (messageText.equals("/start")) {
            telegramUserService.addUser(update.getMessage().getChat());

            sendMainMenu(chatId, telegramClient);
        } else if(messageText.equals("/show_today_medication_list") || messageText.equals("show_today_medication_list")) {
            telegramMedicationService.prepareAndSendTodayMedicationToTG(chatId, telegramClient);
        } else if(messageText.equals("/menu_medication")) {
            sendMenuMedication(chatId, telegramClient);
        } else {
            sendMessage(chatId, "Unknown operation!", telegramClient);
        }
    }

    @SneakyThrows
    private void handleCallbackQuery(CallbackQuery callbackQuery) {
        var data = callbackQuery.getData();
        var chatId = callbackQuery.getFrom().getId();

        if (data.startsWith("edit_medication:")) {
            Long medicationId = Long.valueOf(data.split(":")[1]);

            try{
             var conversation = telegramMedicationService.editMedication(chatId, medicationId, telegramClient);
             conversations.put(chatId, conversation);
            } catch (Exception e) {
                sendMessage(chatId, "Failed to edit medication!", telegramClient);
            }

            return;
        }

        if (data.startsWith("delete_medication:")) {
            Long medicationId = Long.valueOf(data.split(":")[1]);
            telegramMedicationService.deleteMedication(chatId, medicationId, telegramClient);

            return;
        }

        if (data.startsWith("schedule_medication:")) {
            Long medicationId = Long.valueOf(data.split(":")[1]);

            return;
        }

        switch (data) {
            case "show_today_medication_list" -> {
                telegramMedicationService.prepareAndSendTodayMedicationToTG(chatId, telegramClient);
            }
            case "show_all_medications" -> {
                telegramMedicationService.prepareAndSendMedicationsToTG(chatId, telegramClient);
            }
            case "menu_medication" -> sendMenuMedication(chatId, telegramClient);
            case "add_medication" -> {
                var conversation = telegramMedicationService.addMedication(chatId, telegramClient);
                conversations.put(chatId, conversation);
            }
            default -> sendMessage(chatId, "Unknown operation!", telegramClient);
        }
    }

}
