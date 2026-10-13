package com.schorsche94.medi_track.domain.repository.jpa;

import com.schorsche94.medi_track.domain.entity.MedicationCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MedicationCategoryJpaRepository extends JpaRepository<MedicationCategoryEntity, Long> {
    Optional<MedicationCategoryEntity> findByCodeIgnoreCase(String code);
}
