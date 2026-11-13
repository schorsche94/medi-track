package com.schorsche94.medi_track.telegram_bot.mapper;

import com.schorsche94.medi_track.domain.model.Medication;
import com.schorsche94.medi_track.telegram_bot.model.MedicationConversation;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TelegramMedicationMapper {

    MedicationConversation toConversation(Medication medication);

    Medication toModel(MedicationConversation medicationConversation);
}
