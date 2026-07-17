package com.schorsche94.medi_track.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {

    private Long chatId;

    private String firstName;

    private String lastName;

    private String username;

    private LocalDateTime registeredAt;
}
