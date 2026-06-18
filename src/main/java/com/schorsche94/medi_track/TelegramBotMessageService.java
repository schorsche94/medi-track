package com.schorsche94.medi_track;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;

@Component
public class TelegramBotMessageService implements SpringLongPollingBot {

    private final UpdateConsumer updateConsumer;

    private final String token;

    public TelegramBotMessageService(UpdateConsumer updateConsumer, @Value("${telegram.bot.token}") String token) {
        this.updateConsumer = updateConsumer;
        this.token = token;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return updateConsumer;
    }
}
