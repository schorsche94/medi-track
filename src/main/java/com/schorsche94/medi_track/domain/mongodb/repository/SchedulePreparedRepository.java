package com.schorsche94.medi_track.domain.mongodb.repository;

import com.schorsche94.medi_track.domain.mongodb.document.SchedulePrepared;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;

public interface SchedulePreparedRepository extends MongoRepository<SchedulePrepared, String> {

    boolean existsByDate(LocalDate date);

    default void markPrepared(LocalDate date) {
        if (!existsByDate(date)) {
            save(new SchedulePrepared(date));
        }
    }
}
