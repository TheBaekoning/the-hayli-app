package app.asclepius.service;

import app.asclepius.model.NewUserRequest;
import app.asclepius.model.UserResponse;

import java.io.IOException;

public interface AccountService {
    UserResponse userRegistration(NewUserRequest userRequest) throws IOException;

    void sendVerificationEmail(String email) throws IOException;

    boolean userVerification(String encoding);

    void sentForgotEmail(String email) throws IOException;
}
