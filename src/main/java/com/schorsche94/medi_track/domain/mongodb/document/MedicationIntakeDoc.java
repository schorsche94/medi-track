package com.schorsche94.medi_track.domain.mongodb.document;

import com.schorsche94.medi_track.domain.model.MedicationIntakeDetails;
import com.schorsche94.medi_track.domain.mongodb.enums.IntakeStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document("medication_intakes")
public class MedicationIntakeDoc {

    @Id
    private String id;

    private String userId;
    private MedicationIntakeDetails medication;
    private String scheduledLocal;
    private String timeZone;
    private Instant scheduledAt;
    private String dosage;

    @Builder.Default
    private boolean publishedToKafka = false;

    @Builder.Default
    private IntakeStatus status = IntakeStatus.PENDING;

    private Instant statusUpdatedAt;
    private String statusUpdatedAtLocal;

    private Instant takenAt;
    private Instant postponedTo;
    private String skipReason;

    private Instant createdAt;
    private Instant updatedAt;

}
