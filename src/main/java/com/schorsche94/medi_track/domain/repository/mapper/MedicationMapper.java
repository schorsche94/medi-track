package com.schorsche94.medi_track.domain.repository.mapper;

import com.schorsche94.medi_track.domain.entity.MedicationEntity;
import com.schorsche94.medi_track.domain.model.Medication;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class MedicationMapper {

    @Mapping(source = "addedBy", target = "user.chatId")
    public abstract MedicationEntity toEntity(Medication model);

    @Mapping(source = "user.chatId", target = "addedBy")
    public abstract Medication toModel(MedicationEntity entity);

    public List<MedicationEntity> toEntityList(List<Medication> source) {
        return source.stream().map(this::toEntity).toList();
    }

    public List<Medication> toModelList(List<MedicationEntity> source) {
        return source.stream().map(this::toModel).toList();
    }

}
