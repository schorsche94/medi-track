package com.schorsche94.medi_track.service.impl;

import com.schorsche94.medi_track.domain.model.MedicationCategory;
import com.schorsche94.medi_track.domain.repository.MedicationCategoryRepository;
import com.schorsche94.medi_track.service.MedicationCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicationCategoryServiceImpl implements MedicationCategoryService {

    private final MedicationCategoryRepository repository;

    public List<MedicationCategory> getCategories() { return repository.getCategories(); }
    public MedicationCategory createCategory(MedicationCategory category) { return repository.createCategory(category); }
    public MedicationCategory updateCategory(MedicationCategory category) { return repository.updateCategory(category); }
    public MedicationCategory getCategory(Long id) { return repository.getCategory(id); }
    public MedicationCategory getCategoryByCode(String code) { return repository.getCategoryByCode(code); }
    public void deleteCategory(Long id) { repository.deleteCategory(id); }
}
