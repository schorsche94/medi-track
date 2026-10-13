package com.schorsche94.medi_track.service.impl;

import com.schorsche94.medi_track.domain.model.MedicationForm;
import com.schorsche94.medi_track.domain.repository.MedicationFormRepository;
import com.schorsche94.medi_track.service.MedicationFormService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicationFormServiceImpl implements MedicationFormService {

    private final MedicationFormRepository repository;

    public List<MedicationForm> getForms() { return repository.getForms(); }
    public MedicationForm createForm(MedicationForm form) { return repository.createForm(form); }
    public MedicationForm updateForm(MedicationForm form) { return repository.updateForm(form); }
    public MedicationForm getForm(Long id) { return repository.getForm(id); }
    public MedicationForm getFormByCode(String code) { return repository.getFormByCode(code); }
    public void deleteForm(Long id) { repository.deleteForm(id); }
}
