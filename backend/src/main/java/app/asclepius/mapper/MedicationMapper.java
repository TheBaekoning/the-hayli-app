package app.asclepius.mapper;

import app.asclepius.entity.MedicationEntity;
import app.asclepius.model.MedicationRequest;
import app.asclepius.model.MedicationResponse;
import org.mapstruct.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MedicationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    MedicationEntity medicationRequestToEntity(MedicationRequest medicationRequest);

    MedicationResponse medicationEntityToResponse(MedicationEntity medication);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    void updateMedicationRequestToEntity(MedicationRequest request, @MappingTarget MedicationEntity medication);
}
