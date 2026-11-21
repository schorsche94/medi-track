package com.schorsche94.medi_track.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.schorsche94.medi_track.domain.model.MedicationIntake;
import com.schorsche94.medi_track.domain.model.Medication;
import com.schorsche94.medi_track.domain.model.MedicationIntakeDetails;
import com.schorsche94.medi_track.domain.model.Schedule;
import com.schorsche94.medi_track.domain.model.ScheduledTimeDosage;
import com.schorsche94.medi_track.domain.mongodb.enums.IntakeStatus;
import com.schorsche94.medi_track.domain.mongodb.mapper.MedicationIntakeMapper;
import com.schorsche94.medi_track.domain.mongodb.repository.MedicationIntakeRepository;
import com.schorsche94.medi_track.domain.mongodb.repository.SchedulePreparedRepository;
import com.schorsche94.medi_track.domain.repository.mapper.MedicationMapper;
import com.schorsche94.medi_track.domain.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import static com.schorsche94.medi_track.domain.enums.ScheduleStatus.ACTIVE;

@Service
@RequiredArgsConstructor
public class DailyScheduleService {

    private static final Logger log = LoggerFactory.getLogger(DailyScheduleService.class);

    private final SchedulePreparedRepository preparedRepository;
    private final MedicationIntakeRepository intakeRepository;
    private final MedicationIntakeMapper intakeMapper;
    private final ScheduleRepository scheduleRepository;
    private final MedicationMapper medicationMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void prepareIfNeeded(LocalDate date) {
        if (preparedRepository.existsByDate(date)) {
            log.info("Schedule for {} is already prepared — skipping", date);
            return;
        }

        log.info("Preparing schedule for {}", date);

        try {
            List<MedicationIntake> intakes = buildIntakesForDate(date);

            if (!intakes.isEmpty()) {
                intakeRepository.saveAll(intakeMapper.toDocumentList(intakes));
                log.info("Created {} medication intake records for {}", intakes.size(), date);

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

        List<Schedule> todaySchedules = scheduleRepository.getSchedulesForDate(date);
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

                MedicationIntakeDetails medication = createMedicationSnapshot(schedule);
                for (ScheduledTimeDosage scheduledTime : scheduledTimes) {
                    result.add(createIntake(
                            String.valueOf(schedule.getUser().getChatId()),
                            medication,
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
                                          MedicationIntakeDetails medication,
                                          LocalDate date,
                                          LocalTime time,
                                          String dosage) {

        ZoneId scheduleZone = ZoneId.of("Europe/Chisinau");
        LocalDateTime scheduledLocal = LocalDateTime.of(date, time);
        Instant scheduledAt = scheduledLocal.atZone(scheduleZone).toInstant();

        Instant now = Instant.now();

        MedicationIntake intake = new MedicationIntake();
        intake.setUserId(userId);
        intake.setMedication(medication);
        intake.setScheduledLocal(scheduledLocal.toString());
        intake.setTimeZone(scheduleZone.getId());
        intake.setScheduledAt(scheduledAt);
        intake.setDosage(dosage);
        intake.setStatus(IntakeStatus.PENDING);
        intake.setCreatedAt(now);
        intake.setUpdatedAt(now);

        return intake;
    }

    private MedicationIntakeDetails createMedicationSnapshot(Schedule schedule) {
        Medication medication = medicationMapper.toModel(schedule.getMedicine());
        return MedicationIntakeDetails.builder()
                .id(medication.getId())
                .createdDate(medication.getCreatedDate())
                .updatedDate(medication.getUpdatedDate())
                .name(medication.getName())
                .activeSubstance(medication.getActiveSubstance())
                .dosage(medication.getDosage())
                .form(medication.getForm())
                .category(medication.getCategory())
                .description(medication.getDescription())
                .instructions(medication.getInstructions())
                .addedBy(medication.getAddedBy())
                .intakeInstruction(schedule.getIntakeInstruction())
                .customInstruction(schedule.getCustomInstruction())
                .build();
    }
}
