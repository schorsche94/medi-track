package com.schorsche94.medi_track.domain.mongodb.repository;

import com.schorsche94.medi_track.domain.mongodb.document.MedicationIntakeDoc;
import com.schorsche94.medi_track.domain.mongodb.enums.IntakeStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.Instant;
import java.util.List;

public interface MedicationIntakeRepository extends MongoRepository<MedicationIntakeDoc, String> {

    List<MedicationIntakeDoc> findByStatusAndPublishedToKafkaFalseAndScheduledAtLessThanEqual(
            IntakeStatus status,
            Instant scheduledAt
    );

    MedicationIntakeDoc findByMedicationIdAndUserId(String medicationId, String userId);

    MedicationIntakeDoc findByIdAndUserId(String medicationId, String userId);
}
