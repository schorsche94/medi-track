package com.schorsche94.medi_track.telegram_bot.model;

import com.schorsche94.medi_track.domain.enums.IntakeInstruction;
import com.schorsche94.medi_track.domain.enums.ScheduleStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
public class ScheduleConversation {
    private ConversationState state;

    private String id;

    private String medicineId;

    private String chatId;

    private LocalDate startDate;

    private Integer durationDays;

    private LocalDate endDate;

    private Integer frequencyPerDay;

    private String scheduledTimes;

    private Integer intervalDays;

    private Integer pauseDays;

    private IntakeInstruction intakeInstruction;

    private String customInstruction;

    private ScheduleStatus status;

}
