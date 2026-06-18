package com.schorsche94.medi_track.api.service.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Medication {

    private String id;
    private String name;
    private String description;
    private BigDecimal doze;
    private String dozeType;
    private MedicationForm medicationForm;

}
