package com.schorsche94.medi_track.domain.repository.jpa;

import com.schorsche94.medi_track.domain.entity.MedicationFormEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MedicationFormJpaRepository extends JpaRepository<MedicationFormEntity, Long> {
    Optional<MedicationFormEntity> findByCodeIgnoreCase(String code);
}
