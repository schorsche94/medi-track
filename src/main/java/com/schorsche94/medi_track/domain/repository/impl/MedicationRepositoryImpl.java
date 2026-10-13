package com.schorsche94.medi_track.domain.repository.impl;

import com.schorsche94.medi_track.domain.model.Medication;
import com.schorsche94.medi_track.domain.repository.jpa.MedicationJpaRepository;
import com.schorsche94.medi_track.domain.repository.jpa.MedicationCategoryJpaRepository;
import com.schorsche94.medi_track.domain.repository.jpa.MedicationFormJpaRepository;
import com.schorsche94.medi_track.domain.repository.MedicationRepository;
import com.schorsche94.medi_track.domain.repository.mapper.MedicationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Transactional
public class MedicationRepositoryImpl implements MedicationRepository {

    @Autowired
    private MedicationJpaRepository repository;

    @Autowired
    private MedicationMapper mapper;

    @Autowired
    private MedicationCategoryJpaRepository categoryRepository;

    @Autowired
    private MedicationFormJpaRepository formRepository;

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
        setExistingReferences(entity);
        entity = repository.save(entity);
        return mapper.toModel(entity);
    }

    @Override
    public Medication updateMedication(Medication medication) {
        var entity = mapper.toEntity(medication);
        setExistingReferences(entity);
        entity = repository.save(entity);
        return mapper.toModel(entity);
    }

    @Override
    public Medication getMedication(String id) {
        return repository.findById(Long.valueOf(id)).map(mapper::toModel).orElse(null);
    }

    @Override
    public void deleteMedication(String id) {
        repository.deleteById(Long.valueOf(id));
    }

    private void setExistingReferences(com.schorsche94.medi_track.domain.entity.MedicationEntity entity) {
        if (entity.getCategory() != null && entity.getCategory().getId() != null) {
            entity.setCategory(categoryRepository.getReferenceById(entity.getCategory().getId()));
        }
        if (entity.getForm() != null && entity.getForm().getId() != null) {
            entity.setForm(formRepository.getReferenceById(entity.getForm().getId()));
        }
    }
}
