package com.schorsche94.medi_track.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.schorsche94.medi_track.domain.model.Schedule;
import com.schorsche94.medi_track.domain.model.ScheduledTimeDosage;
import com.schorsche94.medi_track.domain.mongodb.document.MedicationIntake;
import com.schorsche94.medi_track.domain.mongodb.enums.IntakeStatus;
import com.schorsche94.medi_track.domain.mongodb.repository.MedicationIntakeRepository;
import com.schorsche94.medi_track.domain.mongodb.repository.SchedulePreparedRepository;
import com.schorsche94.medi_track.domain.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import static com.schorsche94.medi_track.domain.enums.ScheduleStatus.ACTIVE;

@Service
@RequiredArgsConstructor
public class DailyScheduleService {

    private static final Logger log = LoggerFactory.getLogger(DailyScheduleService.class);

    private final SchedulePreparedRepository preparedRepository;
    private final MedicationIntakeRepository intakeRepository;
    private final ScheduleRepository scheduleRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    // private final QueueSender queueSender;

    public void prepareIfNeeded(LocalDate date) {
        if (preparedRepository.existsByDate(date)) {
            log.info("Schedule for {} is already prepared — skipping", date);
            return;
        }

        log.info("Preparing schedule for {}", date);

        try {
            List<MedicationIntake> intakes = buildIntakesForDate(date);

            if (!intakes.isEmpty()) {
                intakeRepository.saveAll(intakes);
                log.info("Created {} medication intake records for {}", intakes.size(), date);

                // TODO: send to the queue
                // queueSender.sendAll(intakes);
            } else {
                log.info("No medication intakes are scheduled for {}", date);
            }

            preparedRepository.markPrepared(date);
            log.info("Schedule for {} prepared successfully", date);

        } catch (Exception e) {
            log.error("Failed to prepare schedule for {}", date, e);
            throw e; // Leave the flag unset so preparation can be retried later.
        }
    }

    private List<MedicationIntake> buildIntakesForDate(LocalDate date) {
        List<MedicationIntake> result = new ArrayList<>();

        List<Schedule> todaySchedules = scheduleRepository.getSchedulesForToday();
        for (Schedule schedule : todaySchedules) {
            if(ACTIVE.equals(schedule.getStatus())) {
                List<ScheduledTimeDosage> scheduledTimes;
                try {
                    scheduledTimes = objectMapper.readValue(
                            schedule.getScheduledTimes(),
                            new TypeReference<>() {}
                    );
                } catch (Exception e) {
                    throw new IllegalArgumentException(
                            "Invalid scheduledTimes JSON for schedule " + schedule.getId(), e
                    );
                }

                for (ScheduledTimeDosage scheduledTime : scheduledTimes) {
                    result.add(createIntake(
                            String.valueOf(schedule.getUser().getChatId()),
                            String.valueOf(schedule.getMedicine().getId()),
                            schedule.getMedicine().getName(),
                            date,
                            LocalTime.parse(scheduledTime.time()),
                            scheduledTime.dosage()
                    ));
                }
            }
        }

        return result;
    }

    private MedicationIntake createIntake(String userId,
                                          String medicationId,
                                          String medicationName,
                                          LocalDate date,
                                          LocalTime time,
                                          String dosage) {

        Instant scheduledAt = LocalDateTime.of(date, time)
                .toInstant(ZoneOffset.UTC);

        Instant now = Instant.now();

        MedicationIntake intake = new MedicationIntake();
        intake.setUserId(userId);
        intake.setMedicationId(medicationId);
        intake.setMedicationName(medicationName);
        intake.setScheduledAt(scheduledAt);
        intake.setDosage(dosage);
        intake.setStatus(IntakeStatus.PENDING);
        intake.setCreatedAt(now);
        intake.setUpdatedAt(now);

        return intake;
    }
}
