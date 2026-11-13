package com.schorsche94.medi_track.domain.repository;

import com.schorsche94.medi_track.domain.model.Schedule;

import java.util.List;

public interface ScheduleRepository {
    List<Schedule> getSchedules(Long chatId);

    List<Schedule> getSchedulesForToday();

    Schedule createSchedule(Schedule schedule);

    Schedule updateSchedule(Schedule schedule);

    Schedule getSchedule(String id);

    void deleteSchedule(String id);
}
