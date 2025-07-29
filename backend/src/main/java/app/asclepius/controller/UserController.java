package app.asclepius.controller;

import app.asclepius.mapper.UserMapper;
import app.asclepius.model.*;
import app.asclepius.model.response.Error;
import app.asclepius.model.response.Response;
import app.asclepius.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/user")
@Slf4j
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    // SYSTEM USER INFORMATION

    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Parameter(name = "userId", description = "User ID of user stored in the database")
    @Operation(summary = "Retrieve user from database by userId")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved medication list",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponse.class))}),
            @ApiResponse(responseCode = "204", description = "Unable to locate user")}
    )
    public ResponseEntity<Response> getUser(@PathVariable("userId") Integer userId) {
        UserResponse userResponse = userService.retrieveUserById(userId);

        Response response = new Response(userResponse);

        if (userResponse.getUuid() != null) {
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
        }
    }

    @PatchMapping("/{userId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Modify user record")
    @Parameter(name = "userId", description = "User ID of user stored in the database")
    @Parameter(in = ParameterIn.QUERY, name = "_csrf", description = "CSRF query parameter")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created new user"),
            @ApiResponse(responseCode = "404", description = "Invalid id supplied",
                    content = @Content)})
    public ResponseEntity<Response> changeUserPassword(@PathVariable("userId") Integer userId, @RequestBody UserRequest request) {
        Response response = new Response();

        try {
            UserResponse userModified = userService.modifyUser(userId, request);
            response.setData(userModified);
        } catch (EntityNotFoundException e) {
            Error error = new Error(e.getMessage());
            response.setError(error);
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping()
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @Operation(summary = "Retrieve currently authenticated user from database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user informationt",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponse.class))}),
            @ApiResponse(responseCode = "204", description = "Unable to locate user")}
    )
    public ResponseEntity<Response> getUser(Principal principal) {
        UserResponse userResponse = userMapper.userDtoToUserResponse(userService.retrieveUserByUuid(principal.getName()));
        Response response = new Response(userResponse);

        if (principal.getName() != null) {
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
        }
    }

    @PatchMapping()
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Modify current user's record")
    @Parameter(in = ParameterIn.QUERY, name = "_csrf", description = "CSRF query parameter")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created modified user"),
            @ApiResponse(responseCode = "422", description = "Unable to locate user",
                    content = @Content)})
    public ResponseEntity<Response> modifyUser(Principal principal, @RequestBody UserRequest request) {
        UserDto user = userService.retrieveUserByUuid(principal.getName());
        Response response = new Response();

        try {
            UserResponse userModified = userService.modifyUser(user.getId(), request);
            response.setData(userModified);
        } catch (EntityNotFoundException e) {
            Error error = new Error(e.getMessage());
            response.setError(error);
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /////

    // ******USER PROFILE INFORMATION******* //

    @GetMapping("/profile")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @Operation(summary = "Retrieve currently authenticated user's profile information from database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user profile information",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserProfileResponse.class)
                    )
            }),
            @ApiResponse(responseCode = "204", description = "Unable to locate user")}
    )
    public ResponseEntity<Response> getUserProfile(Principal principal) {
        Response response = new Response();
        UserDto userDto = userService.retrieveUserByUuid(principal.getName());
        UserProfileResponse userProfileResponse = userService.retrieveUserProfileById(userDto.getId());

        if (userProfileResponse == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
        }

        response.setData(userProfileResponse);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/profile")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @Operation(summary = "Retrieve currently authenticated user's profile information from database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user profile information",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponse.class))})}
    )
    public ResponseEntity<Response> createUserProfile(Principal principal, @RequestBody UserProfileRequest request) {
        Response response = new Response();
        try {
            UserDto userDto = userService.retrieveUserByUuid(principal.getName());
            UserProfileResponse userProfileResponse = userService.createUserProfile(userDto.getId(), request);
            response.setData(userProfileResponse);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (EntityExistsException e) {
            response.setError(new Error(e.getMessage()));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PatchMapping("/profile")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @Operation(summary = "Retrieve currently authenticated user's profile information from database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully updated profile record for logged in user",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponse.class))}),
            @ApiResponse(responseCode = "422", description = "Unable to locate user profile to modify")}
    )
    public ResponseEntity<Response> updateUserProfile(Principal principal, @RequestBody UserProfileRequest request) {
        Response response = new Response();
        UserDto userDto = userService.retrieveUserByUuid(principal.getName());
        try {
            UserProfileResponse userProfileResponse = userService.modifyUserProfile(userDto.getId(), request);
            response.setData(userProfileResponse);
        } catch (EntityNotFoundException e) {
            response.setError(new Error(e.getMessage()));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
