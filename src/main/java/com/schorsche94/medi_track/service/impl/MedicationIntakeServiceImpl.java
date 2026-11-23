package com.schorsche94.medi_track.service.impl;

import com.schorsche94.medi_track.domain.model.MedicationIntake;
import com.schorsche94.medi_track.domain.mongodb.document.MedicationIntakeDoc;
import com.schorsche94.medi_track.domain.mongodb.enums.IntakeStatus;
import com.schorsche94.medi_track.domain.mongodb.mapper.MedicationIntakeMapper;
import com.schorsche94.medi_track.domain.mongodb.repository.MedicationIntakeRepository;
import com.schorsche94.medi_track.service.MedicationIntakeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicationIntakeServiceImpl implements MedicationIntakeService {

    private static final ZoneId DEFAULT_ZONE = ZoneId.of("Europe/Chisinau");

    private final MedicationIntakeRepository intakeRepository;
    private final MedicationIntakeMapper medicationIntakeMapper;

    @Override
    public List<MedicationIntake> getIntakesForToday(Long chatId) {
        LocalDate today = LocalDate.now(DEFAULT_ZONE);
        Instant start = today.atStartOfDay(DEFAULT_ZONE).toInstant();
        Instant end = today.plusDays(1).atStartOfDay(DEFAULT_ZONE).toInstant();

        return medicationIntakeMapper.toModelList(
                intakeRepository.findScheduledForUserBetween(
                        start, end, String.valueOf(chatId)));
    }

    @Override
    public void setIntakeStatus(String intakeId, Long chatId, IntakeStatus status) {
        MedicationIntakeDoc intake = intakeRepository.findByIdAndUserId(intakeId, String.valueOf(chatId));
        if (intake == null) {
            return;
        }

        Instant now = Instant.now();
        ZoneId zone = intake.getTimeZone() == null || intake.getTimeZone().isBlank()
                ? DEFAULT_ZONE
                : ZoneId.of(intake.getTimeZone());
        intake.setStatus(status);
        intake.setStatusUpdatedAt(now);
        intake.setStatusUpdatedAtLocal(LocalDateTime.ofInstant(now, zone).toString());
        intakeRepository.save(intake);
    }
}
