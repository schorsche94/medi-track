package com.schorsche94.medi_track.domain.repository;

import com.schorsche94.medi_track.domain.model.Schedule;

import java.time.LocalDate;
import java.util.List;

public interface ScheduleRepository {
    List<Schedule> getSchedules(Long chatId);

    List<Schedule> getSchedulesForDate(LocalDate date);

    Schedule createSchedule(Schedule schedule);

    Schedule updateSchedule(Schedule schedule);

    Schedule getSchedule(String id);

    void deleteSchedule(String id);

    List<Schedule> getSchedulesByMedicationId(Long chatId, Long medicationId);
}
