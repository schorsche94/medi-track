package com.schorsche94.medi_track.domain.repository.jpa;


import com.schorsche94.medi_track.domain.entity.MedicationEntity;

import com.schorsche94.medi_track.domain.entity.ScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleJpaRepository extends JpaRepository<ScheduleEntity, Long> {

    List<ScheduleEntity> findByUserChatId(Long chatId);

}
