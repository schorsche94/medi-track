package com.schorsche94.medi_track.domain.repository.impl;

import com.schorsche94.medi_track.domain.entity.MedicationCategoryEntity;
import com.schorsche94.medi_track.domain.model.MedicationCategory;
import com.schorsche94.medi_track.domain.repository.MedicationCategoryRepository;
import com.schorsche94.medi_track.domain.repository.jpa.MedicationCategoryJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MedicationCategoryRepositoryImpl implements MedicationCategoryRepository {

    private final MedicationCategoryJpaRepository repository;

    @Override
    public List<MedicationCategory> getCategories() {
        return repository.findAll().stream().map(this::toModel).toList();
    }

    @Override
    public MedicationCategory createCategory(MedicationCategory category) {
        return toModel(repository.save(toEntity(category)));
    }

    @Override
    public MedicationCategory updateCategory(MedicationCategory category) {
        var entity = repository.findById(category.getId()).orElseThrow();
        entity.setCode(category.getCode());
        entity.setName(category.getName());
        entity.setDescription(category.getDescription());
        return toModel(repository.save(entity));
    }

    @Override
    public MedicationCategory getCategory(Long id) {
        return repository.findById(id).map(this::toModel).orElse(null);
    }

    @Override
    public MedicationCategory getCategoryByCode(String code) {
        return repository.findByCodeIgnoreCase(code).map(this::toModel).orElse(null);
    }

    @Override
    public void deleteCategory(Long id) {
        repository.deleteById(id);
    }

    private MedicationCategoryEntity toEntity(MedicationCategory model) {
        var entity = new MedicationCategoryEntity();
        entity.setId(model.getId());
        entity.setCode(model.getCode());
        entity.setName(model.getName());
        entity.setDescription(model.getDescription());
        return entity;
    }

    private MedicationCategory toModel(MedicationCategoryEntity entity) {
        return MedicationCategory.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .name(entity.getName())
                .description(entity.getDescription())
                .build();
    }
}
