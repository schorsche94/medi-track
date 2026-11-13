package com.schorsche94.medi_track.service;

import com.schorsche94.medi_track.domain.model.Schedule;

import java.util.List;

public interface ScheduleService {

    List<Schedule> getSchedules(Long chatId);

    List<Schedule> getSchedulesForToday(Long chatId);

    Schedule createSchedule(Schedule schedule, Long chatId);

    Schedule updateSchedule(Schedule schedule, Long chatId);

    Schedule getSchedule(String id, Long chatId);

    void deleteSchedule(String id, Long chatId) throws Exception;
}
