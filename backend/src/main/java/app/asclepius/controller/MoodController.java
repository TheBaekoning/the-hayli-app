package app.asclepius.controller;

import app.asclepius.model.*;
import app.asclepius.model.response.Response;
import app.asclepius.service.MoodService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/mood")
public class MoodController {
    private final MoodService moodService;

    @GetMapping()
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Retrieves paginated mood record for currently authenticated user. " +
            "List is ordered by entry date (ascending) *Note* entry date is not database record creation date")
    @Parameter(in = ParameterIn.QUERY, name = "page", description = "Retrieve paginated list of mood records - 1 based start")
    @Parameter(in = ParameterIn.QUERY, name = "pageSize", description = "How many items to return per page")
    @Parameter(in = ParameterIn.QUERY, name = "start", description = "Retrieve list of mood records from this date")
    @Parameter(in = ParameterIn.QUERY, name = "end", description = "Retrieve list of mood records to this date")
    @Parameter(in = ParameterIn.QUERY, name = "order", description = "Retrieve list of mood records by ascending / descending order by mood date (defaults descending)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retrieved list"),
            @ApiResponse(responseCode = "403", description = "Access Denied.",
                    content = @Content)})
    public ResponseEntity<Response> getMoodRecordsPaginated(Principal principal,
                                                            @RequestParam(value = "start", required = false) LocalDate start,
                                                            @RequestParam(value = "end", required = false) LocalDate end,
                                                            @Parameter Integer page,
                                                            @Parameter Integer pageSize,
                                                            @Parameter String order) {
        Response response = new Response();
        log.info("Retrieving mood list for user: {} ", principal.getName());
        page = page - 1;
        MoodResponse moodResponse = moodService.retrieveMoodList(start, end, page, pageSize, principal.getName(), order);
        response.setData(moodResponse);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{moodId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Retrieves mood response with a single entity of mood id that was requested")
    @Parameter(in = ParameterIn.PATH, name = "moodId", description = "Retrieve by moodId")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retrieved mood id"),
            @ApiResponse(responseCode = "403", description = "Access Denied.",
                    content = @Content)})
    public ResponseEntity<Response> getMoodRecordById(Principal principal,
                                                          @PathVariable(value = "moodId") Integer moodId) {
        Response response = new Response();
        log.info("Retrieving mood id {} for user: {} ", moodId, principal.getName());
        MoodResponse moodResponse = moodService.retrieveMoodById(moodId, principal.getName());
        response.setData(moodResponse);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping()
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Creates a new mood record(s) for the currently authenticated user")
    @Parameter(in = ParameterIn.QUERY, name = "_csrf", description = "CSRF query parameter")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully added mood record for user"),
            @ApiResponse(responseCode = "403", description = "Access Denied.",
                    content = @Content)})
    public ResponseEntity<Response> createMoodRecords(Principal principal, @RequestBody MoodRequest moodRequest) {
        Response response = new Response();
        log.info("Creating + " + moodRequest.getMoodEntries().size() + " mood entries for user: " + principal.getName());
        List<MoodDto> moodResponse = moodService.createMoodRecords(moodRequest, principal.getName());
        response.setData(moodResponse);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping()
    @PreAuthorize("hasRole('ROLE_USER')")
    @Parameter(in = ParameterIn.QUERY, name = "_csrf", description = "CSRF query parameter")
    @Operation(summary = "Updates mood record of the given mood record ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully updated mood record for user"),
            @ApiResponse(responseCode = "403", description = "Access Denied.",
                    content = @Content),
            @ApiResponse(responseCode = "422", description = "Unable to locate record to modify")})
    public ResponseEntity<Response> updateMoodRecord(Principal principal, @RequestBody MoodEntryRequest moodRequest) {
        Response response = new Response();
        log.info("Updating mood entry id: {} for user: {}", moodRequest.getId(), principal.getName());
        try {
            MoodDto moodDto = moodService.updateMoodRecord(moodRequest, principal.getName());
            response.setData(moodDto);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{moodId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Parameter(in = ParameterIn.QUERY, name = "_csrf", description = "CSRF query parameter")
    @Parameter(name = "moodId", description = "mood record ID stored in the database")
    @Operation(summary = "Deletes mood record by mood id from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted mood record for user"),
            @ApiResponse(responseCode = "403", description = "Access Denied.",
                    content = @Content)})
    public ResponseEntity<Void> deleteMoodRecord(Principal principal, @PathVariable(value = "moodId") Integer moodId) {
        log.info("Deleting mood entry id: {} for user: {}", moodId, principal.getName());
        moodService.deleteMoodRecord(moodId, principal.getName());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping()
    @PreAuthorize("hasRole('ROLE_USER')")
    @Parameter(in = ParameterIn.QUERY, name = "_csrf", description = "CSRF query parameter")
    @Operation(summary = "Deletes mood records by a list of integer ids")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted mood record for user"),
            @ApiResponse(responseCode = "403", description = "Access Denied.",
                    content = @Content)})
    public ResponseEntity<Response> deleteBulkMoods(Principal principal, @RequestBody MoodBulkDeleteRequest moodIds) {
        Response response = new Response();
        log.info("Deleting bulk mood ids for user: {}", principal.getName());
        MoodBulkDeleteResponse moodBulkDeleteResponse = moodService.deleteMoodRecordsByList(moodIds, principal.getName());
        response.setData(moodBulkDeleteResponse);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
