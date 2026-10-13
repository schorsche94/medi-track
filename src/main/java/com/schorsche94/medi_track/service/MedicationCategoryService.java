package com.schorsche94.medi_track.service;

import com.schorsche94.medi_track.domain.model.MedicationCategory;

import java.util.List;

public interface MedicationCategoryService {
    List<MedicationCategory> getCategories();
    MedicationCategory createCategory(MedicationCategory category);
    MedicationCategory updateCategory(MedicationCategory category);
    MedicationCategory getCategory(Long id);
    MedicationCategory getCategoryByCode(String code);
    void deleteCategory(Long id);
}
