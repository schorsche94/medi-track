package com.schorsche94.medi_track;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class UpdateConsumer implements LongPollingSingleThreadUpdateConsumer {
    private final TelegramClient telegramClient;

    private String token;

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
            } else if(messageText.equals("/keyboard")) {
                sendReplyKeyboard(chatId);
            } else if(messageText.equals("Hello")) {
                sendMyName(chatId, update.getMessage().getFrom());
            } else if(messageText.equals("Pic")) {
                sendImage(chatId);
            } else {
                sendMessage(chatId, "I don`t understand you");
            }

        } else if (update.hasCallbackQuery()) {
            handleCallbackQuery(update.getCallbackQuery());

        }
    }

    @SneakyThrows
    private void sendReplyKeyboard(Long chatId) {
        SendMessage sendMessage = SendMessage.builder()
                .text("example of the keyboard")
                .chatId(chatId)
                .build();
        List<KeyboardRow> keyboardRows = List.of(new KeyboardRow("Hello", "Pic"));
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup(keyboardRows);
        sendMessage.setReplyMarkup(markup);
        telegramClient.execute(sendMessage);
    }

    private void handleCallbackQuery(CallbackQuery callbackQuery) {
        var data = callbackQuery.getData();
        var chatId = callbackQuery.getFrom().getId();
        var user = callbackQuery.getFrom();
        switch (data) {
            case "my_name" -> sendMyName(chatId, user);
            case "random" -> sendRandom(chatId);
            case "long_process" -> sendImage(chatId);
            default -> sendMessage(chatId, "Unknown operation");
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

    private void sendImage(Long chatId) {

        sendMessage(chatId, "Sending image...");
        new Thread(() ->{
            var imageUrl = "https://picsum.photos/200";
            try {
                URL url = new URL(imageUrl);
                var inputStream = url.openStream();

                SendPhoto sendPhoto = SendPhoto.builder()
                        .chatId(chatId)
                        .photo(new InputFile(inputStream, "random.jpg"))
                        .caption("Your random pic")
                        .build();
                telegramClient.execute(sendPhoto);
            } catch (IOException | TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    private void sendRandom(Long chatId) {
        var randomInt = ThreadLocalRandom.current().nextInt();
        sendMessage(chatId, "Your random number is: " + randomInt);
    }

    private void sendMyName(Long chatId, User user) {
        var text = "Hello ! \n\n Your name is: %s\n Your nickname is: %s\n".formatted(user.getFirstName() + " " + user.getLastName(),
                user.getUserName());
        sendMessage(chatId, text);

    }

    @SneakyThrows
    private void sendMainMenu(Long chatId) {
        SendMessage message = SendMessage.builder()
                .text("Welcome! Choose what you want to do!")
                .chatId(chatId)
                .build();
        var button1 = InlineKeyboardButton.builder().text("What is your name?").callbackData("my_name").build();
        var button2 = InlineKeyboardButton.builder().text("Get a number").callbackData("random").build();
        var button3 = InlineKeyboardButton.builder().text("Long process").callbackData("long_process").build();
        List<InlineKeyboardRow> keyboardRows = List.of(
                new InlineKeyboardRow(button1),
                new InlineKeyboardRow(button2),
                new InlineKeyboardRow(button3));
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(keyboardRows);
        message.setReplyMarkup(markup);
        telegramClient.execute(message);
    }
}
