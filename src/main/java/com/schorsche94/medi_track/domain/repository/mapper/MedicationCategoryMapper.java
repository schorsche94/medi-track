package com.schorsche94.medi_track.domain.repository.mapper;

import com.schorsche94.medi_track.domain.entity.MedicationCategoryEntity;
import com.schorsche94.medi_track.domain.model.MedicationCategory;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MedicationCategoryMapper {
    MedicationCategoryEntity toEntity(MedicationCategory model);
    MedicationCategory toModel(MedicationCategoryEntity entity);
}
