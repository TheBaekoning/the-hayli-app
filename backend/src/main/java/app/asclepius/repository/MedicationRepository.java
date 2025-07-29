package app.asclepius.repository;

import app.asclepius.entity.MedicationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicationRepository extends JpaRepository<MedicationEntity, Integer> {
    List<MedicationEntity> getMedicationsEntitiesByUserId(Integer userId);
}
