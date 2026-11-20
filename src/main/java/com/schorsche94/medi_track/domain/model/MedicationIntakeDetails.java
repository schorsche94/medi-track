package com.schorsche94.medi_track.domain.model;

import com.schorsche94.medi_track.domain.enums.IntakeInstruction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MedicationIntakeDetails {

    private String id;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private String name;
    private String activeSubstance;
    private String dosage;
    private MedicationForm form;
    private MedicationCategory category;
    private String description;
    private String instructions;
    private Long addedBy;

    private IntakeInstruction intakeInstruction;
    private String customInstruction;
}
