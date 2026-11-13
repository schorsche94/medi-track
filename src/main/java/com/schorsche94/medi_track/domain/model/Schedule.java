package com.schorsche94.medi_track.domain.model;

import com.schorsche94.medi_track.domain.entity.MedicationEntity;
import com.schorsche94.medi_track.domain.entity.UserEntity;
import com.schorsche94.medi_track.domain.enums.IntakeInstruction;
import com.schorsche94.medi_track.domain.enums.ScheduleStatus;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Schedule {
    private Long id;

    private MedicationEntity medicine;

    private UserEntity user;

    private LocalDate startDate;

    private Integer durationDays;

    private LocalDate endDate;

    private Integer frequencyPerDay;

    /**
     * JSON array containing scheduled times and dosage.
     * Example for 2 times a day:
     * [
     *   {"time": "08:00", "dosage": "1 tablet"},
     *   {"time": "20:00", "dosage": "500 mg"}
     * ]
     */
    private String scheduledTimes;

    /** Take medicine every X days (e.g. every 2 days) */
    private Integer intervalDays;

    /** Pause duration in days after completing the course */
    private Integer pauseDays;

    /** Special intake instruction */

    private IntakeInstruction intakeInstruction;

    /** Additional custom instruction if OTHER is selected */
    private String customInstruction;

    /** Current status of the schedule */
    private ScheduleStatus status;

}
