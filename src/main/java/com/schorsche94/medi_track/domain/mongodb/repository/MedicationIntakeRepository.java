package com.schorsche94.medi_track.domain.mongodb.repository;

import com.schorsche94.medi_track.domain.mongodb.document.MedicationIntakeDoc;
import com.schorsche94.medi_track.domain.mongodb.enums.IntakeStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.Instant;
import java.util.List;

public interface MedicationIntakeRepository extends MongoRepository<MedicationIntakeDoc, String> {

    List<MedicationIntakeDoc> findByStatusAndPublishedToKafkaFalseAndScheduledAtLessThanEqual(
            IntakeStatus status,
            Instant scheduledAt
    );

    MedicationIntakeDoc findByIdAndUserId(String intakeId, String userId);

    @Query("{ 'scheduledAt': { '$gte': ?0, '$lt': ?1 }, 'userId': ?2 }")
    List<MedicationIntakeDoc> findScheduledForUserBetween(
            Instant start,
            Instant end,
            String userId
    );
}
