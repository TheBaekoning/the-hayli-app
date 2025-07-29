package app.asclepius.model;

import lombok.Data;

@Data
public class MedicationResponse {
    private Integer id;
    private String dosage;
    private String medicationName;
}
