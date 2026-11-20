package com.schorsche94.medi_track.domain.model;

import com.schorsche94.medi_track.domain.mongodb.enums.IntakeStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MedicationIntake {
    private String id;

    private String userId;
    private MedicationIntakeDetails medication;

    // Local wall-clock value for display/audit; scheduledAt remains the UTC instant used for dispatch.
    private String scheduledLocal;
    private String timeZone;
    private Instant scheduledAt;
    private String dosage;
    private boolean publishedToKafka;

    private IntakeStatus status;

    private Instant takenAt;
    private Instant postponedTo;
    private String skipReason;

    private Instant createdAt;
    private Instant updatedAt;
}
