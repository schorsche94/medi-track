package com.schorsche94.medi_track.telegram_bot.service;

import com.schorsche94.medi_track.telegram_bot.UpdateConsumer;
import com.schorsche94.medi_track.telegram_bot.config.TelegramBotConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;

import static com.schorsche94.medi_track.telegram_bot.config.TelegramBotConfig.botToken;


@Component
public class TelegramBotMessageService implements SpringLongPollingBot {

    private final UpdateConsumer updateConsumer;

    public TelegramBotMessageService(UpdateConsumer updateConsumer) {
        this.updateConsumer = updateConsumer;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return updateConsumer;
    }
}
