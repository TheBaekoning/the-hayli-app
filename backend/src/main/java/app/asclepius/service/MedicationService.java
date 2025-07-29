package app.asclepius.service;

import app.asclepius.model.MedicationRequest;
import app.asclepius.model.MedicationResponse;

import java.util.List;

public interface MedicationService {
    List<MedicationResponse> getAllMedications(Integer userId);

    void addMedication(MedicationRequest request, Integer userId);

    void updateMedication(MedicationRequest request, Integer medicationId);

    void deleteMedication(Integer medicationId);
}
