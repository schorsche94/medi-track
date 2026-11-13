package com.schorsche94.medi_track.telegram_bot.mapper;

import com.schorsche94.medi_track.domain.model.Schedule;
import com.schorsche94.medi_track.telegram_bot.model.ScheduleConversation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TelegramScheduleMapper {

    @Mapping(source = "medicine.id", target = "medicineId")
    ScheduleConversation toConversation(Schedule schedule);

    @Mapping(source = "medicineId", target = "medicine.id")
    Schedule toModel(ScheduleConversation scheduleConversation);
}
