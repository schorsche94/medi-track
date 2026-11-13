package com.schorsche94.medi_track.service.impl;

import com.schorsche94.medi_track.domain.model.Schedule;
import com.schorsche94.medi_track.domain.repository.ScheduleRepository;
import com.schorsche94.medi_track.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {
    private final ScheduleRepository repository;

    @Override
    public List<Schedule> getSchedules(Long chatId) {
        return repository.getSchedules(chatId);
    }

    @Override
    public List<Schedule> getSchedulesForToday(Long chatId) {
        return repository.getSchedulesForToday();
    }

    @Override
    public Schedule createSchedule(Schedule schedule, Long chatId) {
        schedule.setUser(com.schorsche94.medi_track.domain.entity.UserEntity.builder().chatId(chatId).build());
        return repository.createSchedule(schedule);
    }

    @Override
    public Schedule updateSchedule(Schedule schedule, Long chatId) {
        schedule.setUser(com.schorsche94.medi_track.domain.entity.UserEntity.builder().chatId(chatId).build());
        return repository.updateSchedule(schedule);
    }

    @Override
    public Schedule getSchedule(String id, Long chatId) {
        return repository.getSchedule(id);
    }

    @Override
    public void deleteSchedule(String id, Long chatId) throws Exception {
        repository.deleteSchedule(id);
    }
}
