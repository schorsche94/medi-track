package com.schorsche94.medi_track.service;

import com.schorsche94.medi_track.domain.model.MedicationIntake;
import com.schorsche94.medi_track.domain.mongodb.enums.IntakeStatus;

import java.util.List;

public interface MedicationIntakeService {

    List<MedicationIntake> getIntakesForToday(Long chatId);

    void setIntakeStatus(String intakeId, Long chatId, IntakeStatus status);
}
