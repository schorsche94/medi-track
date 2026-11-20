package com.schorsche94.medi_track.domain.mongodb.mapper;

import com.schorsche94.medi_track.domain.model.MedicationIntake;
import com.schorsche94.medi_track.domain.mongodb.document.MedicationIntakeDoc;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MedicationIntakeMapper {

    MedicationIntakeDoc toDocument(MedicationIntake model);

    MedicationIntake toModel(MedicationIntakeDoc document);

    List<MedicationIntakeDoc> toDocumentList(List<MedicationIntake> models);

    List<MedicationIntake> toModelList(List<MedicationIntakeDoc> documents);
}
