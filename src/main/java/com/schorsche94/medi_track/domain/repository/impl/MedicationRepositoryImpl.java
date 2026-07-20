package com.schorsche94.medi_track.domain.repository.impl;

import com.schorsche94.medi_track.domain.model.Medication;
import com.schorsche94.medi_track.domain.repository.jpa.MedicationJpaRepository;
import com.schorsche94.medi_track.domain.repository.MedicationRepository;
import com.schorsche94.medi_track.domain.repository.mapper.MedicationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MedicationRepositoryImpl implements MedicationRepository {

    @Autowired
    private MedicationJpaRepository repository;

    @Autowired
    private MedicationMapper mapper;

    @Override
    public List<Medication> getMedications(Long chatId) {
        return mapper.toModelList(repository.findByUserChatIdOrUserIsNull(chatId));
    }

    @Override
    public List<Medication> getMedicationsForToday() {
        return List.of();
    }

    @Override
    public Medication createMedication(Medication medication) {
        var entity = mapper.toEntity(medication);
        entity = repository.save(entity);
        return mapper.toModel(entity);
    }

    @Override
    public Medication updateMedication(Medication medication) {
        return null;
    }

    @Override
    public Medication getMedication(String id) {
        return null;
    }

    @Override
    public void deleteMedication(String id) {

    }
}
