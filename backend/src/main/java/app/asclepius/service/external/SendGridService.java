package app.asclepius.service.external;

import java.io.IOException;

public interface SendGridService {

    void userExistsEmail(String email) throws IOException;

    void sendVerificationEmail(String email, String token, String uuid) throws IOException;

    void sendForgotEmail(String email, String token, String uuid) throws IOException;
}
