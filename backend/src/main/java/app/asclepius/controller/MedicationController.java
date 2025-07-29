package app.asclepius.controller;

import app.asclepius.model.MedicationRequest;
import app.asclepius.model.MedicationResponse;
import app.asclepius.service.implementation.MedicationServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/medication")
@Slf4j
public class MedicationController {
    private final MedicationServiceImpl medicationService;

    public MedicationController(MedicationServiceImpl medicationService) {
        this.medicationService = medicationService;
    }

    @GetMapping("/list/{userId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Parameter(name = "userId", description = "User ID of user stored in the database")
    @Operation(summary = "Get a list of all medications belonging to userId")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved medication list",
                    content = { @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema( implementation = MedicationResponse.class))) }),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "No medications found for user",
                    content = @Content) })
    public ResponseEntity<List<MedicationResponse>> getListOfMedications(@PathVariable("userId") String userId) {
        Integer findByUserId = Integer.parseUnsignedInt(userId);

        return ResponseEntity.status(HttpStatus.OK).body(medicationService.getAllMedications(findByUserId));
    }

    @PostMapping("/{userId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Parameter(name = "userId", description = "User ID of user stored in the database")
    @Operation(summary = "Add new medication record for user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully added medication for user"),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied",
                    content = @Content)})
    public ResponseEntity<Void> addMedication(@PathVariable("userId") String userId, @RequestBody MedicationRequest request) {
        medicationService.addMedication(request, Integer.valueOf(userId));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping("/{medicationId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Parameter(name = "medicationId", description = "Medication ID of medication stored in the database")
    @Operation(summary = "Update a medication record by medication Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully updated medication"),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied",
                    content = @Content)})
    public ResponseEntity<Void> patchMedication(@PathVariable("medicationId") Integer medicationId, @RequestBody MedicationRequest request) {
        medicationService.updateMedication(request, medicationId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{medicationId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Parameter(name = "medicationId", description = "Medication ID of medication stored in the database")
    @Operation(summary = "Delete a medication record by medication Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted medication"),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied",
                    content = @Content)})
    public ResponseEntity<Void> deleteMedication(@PathVariable("medicationId") Integer medicationId) {
        medicationService.deleteMedication(medicationId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
