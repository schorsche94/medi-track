package com.schorsche94.medi_track.domain.repository.mapper;

import com.schorsche94.medi_track.domain.entity.MedicationEntity;
import com.schorsche94.medi_track.domain.model.Medication;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MedicationMapper {

    @Mapping(source = "addedBy", target = "user.chatId")
    MedicationEntity toEntity(Medication model);

    @Mapping(source = "user.chatId", target = "addedBy")
    Medication toModel(MedicationEntity entity);
}
