package app.asclepius.controller;

import app.asclepius.model.*;
import app.asclepius.model.response.Error;
import app.asclepius.model.response.Response;
import app.asclepius.service.AccountService;
import app.asclepius.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/login")
public class LoginController {
    private static final String UNAUTHORIZED = "Unauthorized Access";

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository =
            new HttpSessionSecurityContextRepository();
    private final AccountService accountService;


    // TODO: handle forbidden response to new response format
    @PostMapping
    @Operation(summary = "Anonymous accessible request to login and retrieve session")
    @Parameter(in = ParameterIn.QUERY, name = "_csrf", description = "CSRF query parameter")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully logged in",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponse.class))}),
            @ApiResponse(responseCode = "403", description = "Request Unauthorized")}
    )
    public ResponseEntity<Response> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request, HttpServletResponse response) {
        HttpHeaders headers = new HttpHeaders();
        Response returnedResponse = new Response();
        LoginResponse loginResponse = new LoginResponse();

        try {
            UserResponse userResponse = userService.retrieveByEmail(loginRequest.getEmail());

            Authentication authenticationRequest =
                    UsernamePasswordAuthenticationToken.unauthenticated(userResponse.getUuid(), loginRequest.getPassword());

            log.info("Attempting to authenticate user: {}", userResponse.getUuid());

            Authentication authenticationResponse =
                    this.authenticationManager.authenticate(authenticationRequest);

            if (authenticationResponse.isAuthenticated()) {
                // invalidates old session inside of apache core
                request.getSession(false).invalidate();
                // creates a new session as per wrapper method from jakarta servlet and assigns new session
                request.getSession(true);

                SecurityContext context = SecurityContextHolder.createEmptyContext();
                context.setAuthentication(authenticationResponse);
                SecurityContextHolder.setContext(context);
                securityContextRepository.saveContext(context, request, response);

                headers.set("Set-Cookie", "JSESSIONID=" + request.getSession(false).getId()
                        + "; Path=/; HttpOnly");
                loginResponse.setLoginSuccess(true);
                returnedResponse.setData(loginResponse);
                return ResponseEntity.status(HttpStatus.OK).headers(headers).body(returnedResponse);
            }

            returnedResponse.setError(new Error(UNAUTHORIZED));
            loginResponse.setLoginSuccess(false);
            returnedResponse.setData(loginResponse);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(returnedResponse);
        } catch (BadCredentialsException e) {
            log.info("Attempted login - Bad credentials");
            returnedResponse.setError(new Error(UNAUTHORIZED));
            loginResponse.setLoginSuccess(false);
            returnedResponse.setData(loginResponse);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(returnedResponse);
        } catch (EntityNotFoundException e) {
            log.info("Attempted login - No Such User with email exists");
            returnedResponse.setError(new Error(UNAUTHORIZED));
            loginResponse.setLoginSuccess(false);
            returnedResponse.setData(loginResponse);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(returnedResponse);
        } catch (Exception e) {
            log.error("User login encountered an error: {}", e.getMessage());
            returnedResponse.setError(new Error(UNAUTHORIZED));
            loginResponse.setLoginSuccess(false);
            returnedResponse.setData(loginResponse);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(returnedResponse);
        }
    }

    @PostMapping("/logout")
    @Operation(summary = "Logs out of session")
    @Parameter(in = ParameterIn.QUERY, name = "_csrf", description = "CSRF query parameter")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully logged out")})
    public ResponseEntity<Response> logout(HttpServletRequest request) {
        try {
            request.logout();
            return ResponseEntity.status(HttpStatus.OK).body(new Response());
        } catch (ServletException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Response(UNAUTHORIZED));
        } catch (Exception e) {
            log.error("User logout encountered an error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Response(UNAUTHORIZED));
        }
    }

    @PostMapping("/register")
    @Operation(summary = "Create new user record")
    @Parameter(in = ParameterIn.QUERY, name = "_csrf", description = "CSRF query parameter")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created new user")})
    public ResponseEntity<Response> registerUser(@RequestBody NewUserRequest request) throws IOException {
        Response response = new Response();
        response.setData(accountService.userRegistration(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/send/verification")
    @Operation(summary = "Resend verification to email")
    @Parameter(in = ParameterIn.QUERY, name = "_csrf", description = "CSRF query parameter")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully sent verification code")
    })
    public ResponseEntity<Response> sendVerification(@RequestBody UserSendVerificationRequest request) throws IOException {
        accountService.sendVerificationEmail(request.getEmail());
        return ResponseEntity.status(HttpStatus.OK).body(new Response());
    }

    @PostMapping("/verify")
    @Operation(summary = "Verifies for unverified user. It receives the base64 encoding and verifies based on uuid and token")
    @Parameter(in = ParameterIn.QUERY, name = "_csrf", description = "CSRF query parameter")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully verified user.")})
    public ResponseEntity<Response> verifyUser(@RequestParam(name = "encoding") String encoding) {
        Response response = new Response();
        UserVerificationResponse verificationResponse = new UserVerificationResponse();

        boolean isVerified = accountService.userVerification(encoding);

        verificationResponse.setVerification(isVerified);
        response.setData(verificationResponse);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/restore")
    @Operation(summary = "Verifies if user is logged in with the session provided")
    @Parameter(in = ParameterIn.QUERY, name = "_csrf", description = "CSRF query parameter")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully verified user is logged in"),
            @ApiResponse(responseCode = "401", description = "Request Unauthorized")}
    )
    public ResponseEntity<Response> restoreSession(HttpServletRequest request) {
        Response response = new Response();
        if (request.getSession(false) != null) {
            return ResponseEntity.status(HttpStatus.OK).body(new Response());
        }
        Error error = new Error("Unauthorized");
        response.setError(error);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @PostMapping("/send/forgot")
    @Operation(summary = "Send email to email address to reset password")
    @Parameter(in = ParameterIn.QUERY, name = "_csrf", description = "CSRF query parameter")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully sent email.")})
    public ResponseEntity<Response> forgotEmail(@RequestBody ForgotAccountRequest forgotAccountRequest) throws IOException {
        ForgotAccountResponse forgotAccountResponse = new ForgotAccountResponse();

        accountService.sentForgotEmail(forgotAccountRequest.getEmail());

        forgotAccountResponse.setSuccess(true);

        Response response = new Response(forgotAccountResponse);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/forgot")
    @Operation(summary = "Verifies for account recovery for user. It receives the base64 encoding and verifies based on uuid and token")
    @Parameter(in = ParameterIn.QUERY, name = "_csrf", description = "CSRF query parameter")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully verified user.")})
    public ResponseEntity<Response> verifyForgotUser(@RequestParam(name = "encoding") String encoding) {
        Response response = new Response();
        ForgotAccountResponse forgotAccountResponse = new ForgotAccountResponse();

        boolean isVerified = accountService.userVerification(encoding);

        forgotAccountResponse.setSuccess(isVerified);
        response.setData(forgotAccountResponse);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
