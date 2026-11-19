package com.schorsche94.medi_track.service;

import com.schorsche94.medi_track.domain.mongodb.document.MedicationIntake;
import com.schorsche94.medi_track.domain.mongodb.enums.IntakeStatus;
import com.schorsche94.medi_track.domain.mongodb.repository.MedicationIntakeRepository;
import com.schorsche94.medi_track.domain.mongodb.repository.SchedulePreparedRepository;
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

@Service
public class DailyScheduleService {

    private static final Logger log = LoggerFactory.getLogger(DailyScheduleService.class);

    private final SchedulePreparedRepository preparedRepository;
    private final MedicationIntakeRepository intakeRepository;
    // private final UserMedicationPlanRepository planRepository;  // source of schedules
    // private final QueueSender queueSender;

    public DailyScheduleService(SchedulePreparedRepository preparedRepository,
                                MedicationIntakeRepository intakeRepository) {
        this.preparedRepository = preparedRepository;
        this.intakeRepository = intakeRepository;
    }

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

    /**
     * The real implementation will go here.
     * This is currently a placeholder to illustrate the expected behavior.
     */
    private List<MedicationIntake> buildIntakesForDate(LocalDate date) {
        List<MedicationIntake> result = new ArrayList<>();

        // TODO: replace with actual user plan retrieval
        // List<UserMedicationPlan> plans = planRepository.findActivePlansForDate(date);
        // for (UserMedicationPlan plan : plans) {
        //     for (LocalTime time : plan.getTimes()) {
        //         result.add(createIntake(plan, date, time));
        //     }
        // }

        // --- Temporary test placeholder ---
        result.add(createIntake(
                "user-1",
                "med-1",
                "Ibuprofen 200 mg",
                date,
                LocalTime.of(9, 0)
        ));
        result.add(createIntake(
                "user-1",
                "med-1",
                "Ibuprofen 200 mg",
                date,
                LocalTime.of(21, 0)
        ));

        return result;
    }

    private MedicationIntake createIntake(String userId,
                                          String medicationId,
                                          String medicationName,
                                          LocalDate date,
                                          LocalTime time) {

        Instant scheduledAt = LocalDateTime.of(date, time)
                .toInstant(ZoneOffset.UTC); // Alternatively, use ZoneId.of("Europe/Moscow").

        Instant now = Instant.now();

        MedicationIntake intake = new MedicationIntake();
        intake.setUserId(userId);
        intake.setMedicationId(medicationId);
        intake.setMedicationName(medicationName);
        intake.setScheduledDate(date);
        intake.setScheduledTime(time);
        intake.setScheduledAt(scheduledAt);
        intake.setStatus(IntakeStatus.PENDING);
        intake.setCreatedAt(now);
        intake.setUpdatedAt(now);

        return intake;
    }
}
