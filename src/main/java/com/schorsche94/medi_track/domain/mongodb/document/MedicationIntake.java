package com.schorsche94.medi_track.domain.mongodb.document;

import com.schorsche94.medi_track.domain.mongodb.enums.IntakeStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document("medication_intakes")
public class MedicationIntake {

    @Id
    private String id;

    private String userId;
    private String medicationId;
    private String medicationName;

    private Instant scheduledAt;
    private String dosage;

    @Builder.Default
    private IntakeStatus status = IntakeStatus.PENDING;

    private Instant takenAt;
    private Instant postponedTo;
    private String skipReason;

    private Instant createdAt;
    private Instant updatedAt;

}
