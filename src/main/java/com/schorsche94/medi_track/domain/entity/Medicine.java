package com.schorsche94.medi_track.domain.entity;

import com.schorsche94.medi_track.domain.enums.DosageType;
import com.schorsche94.medi_track.domain.enums.MedicationForm;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "medicines")
public class Medicine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String activeSubstance;

    private String dosage;

    @Enumerated(EnumType.STRING)
    private DosageType dosageType;

    @Enumerated(EnumType.STRING)
    private MedicationForm form;

    private String instructions;

    private Long chatId;
}
