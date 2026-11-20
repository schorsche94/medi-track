package com.schorsche94.medi_track.kafka;

import com.schorsche94.medi_track.domain.model.MedicationIntake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class MedicationEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(MedicationEventConsumer.class);

    @KafkaListener(
            topics = "medication-events",
            groupId = "${spring.kafka.consumer.group-id:medi-track}"
    )
    public void consume(MedicationIntake intake) {
        log.info("Received medication intake for user {} at {}: {}",
                intake.getUserId(),
                intake.getScheduledAt(),
                intake.getMedication() != null ? intake.getMedication().getName() : "unknown medication");
    }
}
