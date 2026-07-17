package com.schorsche94.medi_track.domain.model;

import com.schorsche94.medi_track.domain.enums.MedicationForm;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Medication {

    private String id;

    private String name;

    private String activeSubstance;

    private String dosage;

    private MedicationForm form;

    private String instructions;

    private Long addedBy;

}
