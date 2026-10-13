package com.schorsche94.medi_track.domain.model;

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

    private MedicationCategory category;

    private String description;

    private String instructions;

    private Long addedBy;

}
