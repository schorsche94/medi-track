package com.schorsche94.medi_track.domain.repository.jpa;


import com.schorsche94.medi_track.domain.entity.ScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ScheduleJpaRepository extends JpaRepository<ScheduleEntity, Long> {

    List<ScheduleEntity> findByUserChatId(Long chatId);

    @Query("""
            SELECT s FROM ScheduleEntity s
            JOIN FETCH s.medicine
            LEFT JOIN FETCH s.user
            WHERE :date BETWEEN s.startDate AND s.endDate
            """)
    List<ScheduleEntity> findByDate(@Param("date") LocalDate date);

}
