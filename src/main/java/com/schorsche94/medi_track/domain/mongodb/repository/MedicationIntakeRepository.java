package com.schorsche94.medi_track.domain.mongodb.repository;

import com.schorsche94.medi_track.domain.mongodb.document.MedicationIntake;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;

public interface MedicationIntakeRepository extends MongoRepository<MedicationIntake, String> {

    boolean existsByScheduledDate(LocalDate date);
}
