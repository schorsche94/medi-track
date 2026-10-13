package com.schorsche94.medi_track.domain.repository.impl;

import com.schorsche94.medi_track.domain.entity.MedicationFormEntity;
import com.schorsche94.medi_track.domain.model.MedicationForm;
import com.schorsche94.medi_track.domain.repository.MedicationFormRepository;
import com.schorsche94.medi_track.domain.repository.jpa.MedicationFormJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MedicationFormRepositoryImpl implements MedicationFormRepository {

    private final MedicationFormJpaRepository repository;

    @Override
    public List<MedicationForm> getForms() {
        return repository.findAll().stream().map(this::toModel).toList();
    }

    @Override
    public MedicationForm createForm(MedicationForm form) {
        return toModel(repository.save(toEntity(form)));
    }

    @Override
    public MedicationForm updateForm(MedicationForm form) {
        var entity = repository.findById(form.getId()).orElseThrow();
        entity.setCode(form.getCode());
        entity.setName(form.getName());
        entity.setDescription(form.getDescription());
        return toModel(repository.save(entity));
    }

    @Override
    public MedicationForm getForm(Long id) {
        return repository.findById(id).map(this::toModel).orElse(null);
    }

    @Override
    public MedicationForm getFormByCode(String code) {
        return repository.findByCodeIgnoreCase(code).map(this::toModel).orElse(null);
    }

    @Override
    public void deleteForm(Long id) {
        repository.deleteById(id);
    }

    private MedicationFormEntity toEntity(MedicationForm model) {
        var entity = new MedicationFormEntity();
        entity.setId(model.getId());
        entity.setCode(model.getCode());
        entity.setName(model.getName());
        entity.setDescription(model.getDescription());
        return entity;
    }

    private MedicationForm toModel(MedicationFormEntity entity) {
        return MedicationForm.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .name(entity.getName())
                .description(entity.getDescription())
                .build();
    }
}
