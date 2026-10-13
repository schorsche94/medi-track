package com.schorsche94.medi_track.domain.repository.mapper;

import com.schorsche94.medi_track.domain.entity.MedicationFormEntity;
import com.schorsche94.medi_track.domain.model.MedicationForm;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MedicationFormMapper {
    MedicationFormEntity toEntity(MedicationForm model);
    MedicationForm toModel(MedicationFormEntity entity);
}
