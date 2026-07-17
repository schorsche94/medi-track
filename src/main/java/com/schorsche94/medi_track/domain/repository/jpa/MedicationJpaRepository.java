package com.schorsche94.medi_track.domain.repository.jpa;


import com.schorsche94.medi_track.domain.entity.MedicationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicationJpaRepository extends JpaRepository<MedicationEntity, Integer> {
}
