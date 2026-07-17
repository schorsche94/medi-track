package com.schorsche94.medi_track.domain.repository;

import com.schorsche94.medi_track.domain.model.Medication;

import java.util.List;

public interface MedicationRepository {
    List<Medication> getMedications();

    List<Medication> getMedicationsForToday();

    Medication createMedication(Medication medication);

    Medication updateMedication(Medication medication);

    Medication getMedication(String id);

    void deleteMedication(String id);
}
