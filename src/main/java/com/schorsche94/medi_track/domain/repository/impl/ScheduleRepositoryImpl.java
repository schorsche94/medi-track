package com.schorsche94.medi_track.domain.repository.impl;

import com.schorsche94.medi_track.domain.entity.ScheduleEntity;
import com.schorsche94.medi_track.domain.model.Schedule;
import com.schorsche94.medi_track.domain.repository.ScheduleRepository;
import com.schorsche94.medi_track.domain.repository.jpa.MedicationJpaRepository;
import com.schorsche94.medi_track.domain.repository.jpa.ScheduleJpaRepository;
import com.schorsche94.medi_track.domain.repository.jpa.UserJpaRepository;
import com.schorsche94.medi_track.domain.repository.mapper.ScheduleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Transactional
public class ScheduleRepositoryImpl implements ScheduleRepository {

    private final ScheduleJpaRepository repository;
    private final MedicationJpaRepository medicationRepository;
    private final UserJpaRepository userRepository;
    private final ScheduleMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<Schedule> getSchedules(Long chatId) {
        return repository.findByUserChatId(chatId).stream().map(mapper::toModel).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Schedule> getSchedulesForToday() {
        return repository.findAll().stream().map(mapper::toModel).toList();
    }

    @Override
    public Schedule createSchedule(Schedule schedule) {
        return mapper.toModel(repository.save(toEntity(schedule)));
    }

    @Override
    public Schedule updateSchedule(Schedule schedule) {
        return mapper.toModel(repository.save(toEntity(schedule)));
    }

    @Override
    @Transactional(readOnly = true)
    public Schedule getSchedule(String id) {
        return repository.findById(Long.valueOf(id)).map(mapper::toModel).orElse(null);
    }

    @Override
    public void deleteSchedule(String id) {
        repository.deleteById(Long.valueOf(id));
    }

    private ScheduleEntity toEntity(Schedule schedule) {
        ScheduleEntity entity = mapper.toEntity(schedule);
        if (schedule.getMedicine() == null || schedule.getMedicine().getId() == null) {
            throw new IllegalArgumentException("A medication is required for a schedule");
        }
        entity.setMedicine(medicationRepository.getReferenceById(schedule.getMedicine().getId()));
        if (schedule.getUser() != null && schedule.getUser().getChatId() != null) {
            entity.setUser(userRepository.getReferenceById(schedule.getUser().getChatId()));
        }
        return entity;
    }
}
