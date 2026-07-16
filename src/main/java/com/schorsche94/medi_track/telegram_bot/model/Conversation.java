package com.schorsche94.medi_track.telegram_bot.model;

import com.schorsche94.medi_track.domain.enums.MedicationForm;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Conversation {
    private ConversationState state;

    private String name;
    private String description;
    private String dosage;
    private MedicationForm medicationForm;

}
