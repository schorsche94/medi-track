package com.schorsche94.medi_track.domain.entity;

import com.schorsche94.medi_track.domain.enums.IntakeInstruction;
import com.schorsche94.medi_track.domain.enums.ScheduleStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "schedules")
public class ScheduleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medicine_id", nullable = false)
    private MedicationEntity medicine;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "added_by", referencedColumnName = "chatId")
    private UserEntity user;

    /** Start date of the medication course */
    private LocalDate startDate;

    /** Duration of the course in days (null = indefinite) */
    private Integer durationDays;

    /** End date of the course */
    private LocalDate endDate;

    /** How many times per day the medicine should be taken */
    private Integer frequencyPerDay;

    /**
     * JSON array containing scheduled times and dosage.
     * Example for 2 times a day:
     * [
     *   {"time": "08:00", "dosage": "1 tablet"},
     *   {"time": "20:00", "dosage": "500 mg"}
     * ]
     */
    @Column(columnDefinition = "jsonb")
    private String scheduledTimes;

    /** Take medicine every X days (e.g. every 2 days) */
    private Integer intervalDays;

    /** Pause duration in days after completing the course */
    private Integer pauseDays;

    /** Special intake instruction */
    @Enumerated(EnumType.STRING)
    private IntakeInstruction intakeInstruction;

    /** Additional custom instruction if OTHER is selected */
    private String customInstruction;

    /** Current status of the schedule */
    @Enumerated(EnumType.STRING)
    private ScheduleStatus status;

}
