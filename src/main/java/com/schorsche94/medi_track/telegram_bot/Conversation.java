package com.schorsche94.medi_track.telegram_bot;

import com.schorsche94.medi_track.domain.enums.MedicationForm;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class Conversation {
    private ConversationState state;

    private String name;
    private String description;
    private BigDecimal doze;
    private String dozeType;
    private MedicationForm medicationForm;

}
