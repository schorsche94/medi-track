package com.schorsche94.medi_track.schedule;

import com.schorsche94.medi_track.domain.mongodb.document.MedicationIntakeDoc;
import com.schorsche94.medi_track.domain.mongodb.enums.IntakeStatus;
import com.schorsche94.medi_track.domain.mongodb.mapper.MedicationIntakeMapper;
import com.schorsche94.medi_track.domain.mongodb.repository.MedicationIntakeRepository;
import com.schorsche94.medi_track.kafka.MedicationEventProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
public class MedicationIntakeDispatchJob {

    private static final Logger log = LoggerFactory.getLogger(MedicationIntakeDispatchJob.class);

    private final MedicationIntakeRepository intakeRepository;
    private final MedicationIntakeMapper intakeMapper;
    private final MedicationEventProducer eventProducer;

    public MedicationIntakeDispatchJob(MedicationIntakeRepository intakeRepository,
                                       MedicationIntakeMapper intakeMapper,
                                       MedicationEventProducer eventProducer) {
        this.intakeRepository = intakeRepository;
        this.intakeMapper = intakeMapper;
        this.eventProducer = eventProducer;
    }

    @Scheduled(cron = "0 * * * * *", zone = "Europe/Chisinau")
    public void dispatchDueIntakes() {
        Instant now = Instant.now();
        List<MedicationIntakeDoc> dueIntakes = intakeRepository
                .findByStatusAndPublishedToKafkaFalseAndScheduledAtLessThanEqual(IntakeStatus.PENDING, now);

        for (MedicationIntakeDoc intake : dueIntakes) {
            try {
                eventProducer.send(intakeMapper.toModel(intake)).get(10, TimeUnit.SECONDS);
                intake.setPublishedToKafka(true);
                intake.setUpdatedAt(Instant.now());
                intakeRepository.save(intake);
                log.info("Published medication intake {} scheduled for {}", intake.getId(), intake.getScheduledAt());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Interrupted while publishing medication intake {}", intake.getId(), e);
                return;
            } catch (ExecutionException | TimeoutException e) {
                log.error("Failed to publish medication intake {}; it will be retried", intake.getId(), e);
            }
        }
    }
}
