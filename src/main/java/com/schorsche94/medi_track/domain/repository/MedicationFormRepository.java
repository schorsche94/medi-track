package com.schorsche94.medi_track.domain.repository;

import com.schorsche94.medi_track.domain.model.MedicationForm;

import java.util.List;

public interface MedicationFormRepository {
    List<MedicationForm> getForms();
    MedicationForm createForm(MedicationForm form);
    MedicationForm updateForm(MedicationForm form);
    MedicationForm getForm(Long id);
    MedicationForm getFormByCode(String code);
    void deleteForm(Long id);
}
