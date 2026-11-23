package com.schorsche94.medi_track.service.impl;

import com.schorsche94.medi_track.domain.model.Schedule;
import com.schorsche94.medi_track.domain.mongodb.document.MedicationIntakeDoc;
import com.schorsche94.medi_track.domain.mongodb.enums.IntakeStatus;
import com.schorsche94.medi_track.domain.mongodb.repository.MedicationIntakeRepository;
import com.schorsche94.medi_track.domain.repository.ScheduleRepository;
import com.schorsche94.medi_track.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {
    private final ScheduleRepository repository;

    @Autowired
    private MedicationIntakeRepository intakeRepository;


    @Override
    public List<Schedule> getSchedules(Long chatId) {
        return repository.getSchedules(chatId);
    }

    @Override
    public List<Schedule> getSchedulesForToday(Long chatId) {
        return repository.getSchedulesForToday();
    }

    @Override
    public Schedule createSchedule(Schedule schedule, Long chatId) {
        schedule.setUser(com.schorsche94.medi_track.domain.entity.UserEntity.builder().chatId(chatId).build());
        return repository.createSchedule(schedule);
    }

    @Override
    public Schedule updateSchedule(Schedule schedule, Long chatId) {
        schedule.setUser(com.schorsche94.medi_track.domain.entity.UserEntity.builder().chatId(chatId).build());
        return repository.updateSchedule(schedule);
    }

    @Override
    public Schedule getSchedule(String id, Long chatId) {
        return repository.getSchedule(id);
    }

    @Override
    public void deleteSchedule(String id, Long chatId) throws Exception {
        repository.deleteSchedule(id);
    }

    @Override
    public void setIntakeMedicationStatus(String medicationIntakeId, Long chatId, IntakeStatus status) {
        MedicationIntakeDoc intake = intakeRepository.findByIdAndUserId(
                medicationIntakeId, String.valueOf(chatId));
        if (intake == null) {
            return;
        }

        Instant now = Instant.now();
        ZoneId zone = ZoneId.of(intake.getTimeZone() == null || intake.getTimeZone().isBlank()
                ? "Europe/Chisinau"
                : intake.getTimeZone());
        intake.setStatus(status);
        intake.setStatusUpdatedAt(now);
        intake.setStatusUpdatedAtLocal(LocalDateTime.ofInstant(now, zone).toString());
        intakeRepository.save(intake);
    }
}
