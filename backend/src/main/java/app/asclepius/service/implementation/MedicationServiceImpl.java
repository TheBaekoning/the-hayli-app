package app.asclepius.service.implementation;

import app.asclepius.entity.MedicationEntity;
import app.asclepius.entity.UserEntity;
import app.asclepius.mapper.MedicationMapper;
import app.asclepius.model.MedicationRequest;
import app.asclepius.model.MedicationResponse;
import app.asclepius.repository.MedicationRepository;
import app.asclepius.repository.UserRepository;
import app.asclepius.service.MedicationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class MedicationServiceImpl implements MedicationService {
    private final MedicationMapper medicationMapper;
    private final MedicationRepository medicationRepository;
    private final UserRepository userRepository;

    @Override
    public List<MedicationResponse> getAllMedications(Integer userId) {
        List<MedicationResponse> medicationResponseList = new ArrayList<>();
        List<MedicationEntity> medicationList = medicationRepository.getMedicationsEntitiesByUserId(userId);
        medicationList.forEach(entity -> medicationResponseList.add(medicationMapper.medicationEntityToResponse(entity)));
        return medicationResponseList;
    }

    @Override
    public void addMedication(MedicationRequest request, Integer userId) {
        Optional<UserEntity> user = userRepository.findById(userId);
        MedicationEntity medication = medicationMapper.medicationRequestToEntity(request);

        user.ifPresentOrElse(userEntity -> {
                    log.info("User found: {}", userId);
                    medication.setUser(userEntity);
                    medicationRepository.save(medication);
                },
                () -> {
                    throw new EntityNotFoundException("User not found");
                });

    }

    @Override
    public void updateMedication(MedicationRequest request, Integer medicationId) {
        Optional<MedicationEntity> medication = medicationRepository.findById(medicationId);

        medication.ifPresentOrElse(
                medicationEntity -> {
                    log.info("Updating medicationId {}, with data {}", medicationId, request);
                    medicationMapper.updateMedicationRequestToEntity(request, medicationEntity);
                    medicationRepository.save(medicationEntity);
                },
                () -> {
                    throw new EntityNotFoundException("Medication not found");
                }
        );
    }

    @Override
    public void deleteMedication(Integer medicationId) {
        Optional<MedicationEntity> medication = medicationRepository.findById(medicationId);

        medication.ifPresentOrElse(
                medicationRepository::delete,
                () -> {
                    throw new EntityNotFoundException("Medication not found");
                }
        );
    }
}
