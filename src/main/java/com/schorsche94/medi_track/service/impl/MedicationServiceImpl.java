package com.schorsche94.medi_track.service.impl;

import com.schorsche94.medi_track.domain.model.Medication;
import com.schorsche94.medi_track.domain.repository.MedicationRepository;
import com.schorsche94.medi_track.service.MedicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MedicationServiceImpl implements MedicationService {

    @Autowired
    private MedicationRepository repository;


    @Override
    public Medication createMedication(Medication medication, Long chatId) {
        medication.setAddedBy(chatId);
        return repository.createMedication(medication);
    }

    @Override
    public List<Medication> getMedications(Long chatId) {
        return repository.getMedications(chatId);
    }

    @Override
    public List<Medication> getMedicationsForToday(Long chatId) {
        return List.of();
    }

    @Override
    public Medication updateMedication(Medication medication, Long chatId) {
        var existing = repository.getMedication(medication.getId());
        if (existing == null || !chatId.equals(existing.getAddedBy())) {
            throw new IllegalArgumentException("Medication not found or you cannot edit it");
        }
        medication.setAddedBy(chatId);
        return repository.updateMedication(medication);
    }

    @Override
    public Medication getMedication(String id, Long chatId) {
        var medication = repository.getMedication(id);
        if (medication == null || !chatId.equals(medication.getAddedBy())) {
            return null;
        }
        return medication;
    }

    @Override
    public void deleteMedication(String id, Long chatId) throws Exception {
        var medication = repository.getMedication(id);
        if (medication == null) {
            throw new Exception("Medication not found");
        }
        if (!chatId.equals(medication.getAddedBy())) {
            throw new Exception("You cannot delete this medication");
        }
        repository.deleteMedication(id);
    }
}
