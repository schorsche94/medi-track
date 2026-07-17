package com.schorsche94.medi_track.service;

import com.schorsche94.medi_track.domain.model.Medication;

import java.util.List;

public interface MedicationService {

    List<Medication> getMedications(Long chatId);

    List<Medication> getMedicationsForToday(Long chatId);

    Medication createMedication(Medication medication, Long chatId);

    Medication updateMedication(Medication medication, Long chatId);

    Medication getMedication(String id, Long chatId);

    void deleteMedication(String id, Long chatId);
}
