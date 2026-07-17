package com.schorsche94.medi_track.telegram_bot.service;

import com.schorsche94.medi_track.domain.model.User;
import com.schorsche94.medi_track.service.UserService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;

@Service
public class TelegramUserService {

    @Autowired
    private UserService service;

    @SneakyThrows
    public void addUser(Chat chat) {
        // TODO: check if already exists
        var user = User.builder()
                .chatId(chat.getId())
                .firstName(chat.getFirstName())
                .lastName(chat.getLastName())
                .username(chat.getUserName())
                .build();
        service.createUser(user);
    }
}
