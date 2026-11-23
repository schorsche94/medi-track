package com.schorsche94.medi_track.domain.repository.mapper;

import com.schorsche94.medi_track.domain.entity.ScheduleEntity;
import com.schorsche94.medi_track.domain.model.Schedule;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {MedicationMapper.class})
public interface ScheduleMapper {
    ScheduleEntity toEntity(Schedule model);

    Schedule toModel(ScheduleEntity entity);
}