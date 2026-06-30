package com.schorsche94.medi_track.domain.enums;

public enum ScheduleStatus {
    ACTIVE,      // The schedule is currently active (user is taking the medicine)
    PAUSED,      // The user is on a pause (e.g. between courses)
    COMPLETED,   // The full course has been finished
    CANCELLED    // The schedule was cancelled by user or doctor
}
