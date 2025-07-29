package app.asclepius.controller;

import app.asclepius.model.ContactDto;
import app.asclepius.model.FriendsAndFamilyResponse;
import app.asclepius.model.response.Response;
import app.asclepius.service.NotificationService;
import app.asclepius.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/ff")
public class FriendsAndFamilyController {
    private final NotificationService notificationService;

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
    public ResponseEntity<Response> retrieveAllContacts(Principal principal) {
        FriendsAndFamilyResponse friendsAndFamilyResponse = new FriendsAndFamilyResponse();
        Response response = new Response();

        List<ContactDto> contactDtoList = notificationService.getFriendsAndFamilies(principal.getName());

        friendsAndFamilyResponse.setContacts(contactDtoList);
        response.setData(friendsAndFamilyResponse);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
