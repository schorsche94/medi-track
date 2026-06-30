package com.schorsche94.medi_track.domain.repository;


import com.schorsche94.medi_track.domain.entity.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicationRepository extends JpaRepository<Medicine, Integer> {
}
