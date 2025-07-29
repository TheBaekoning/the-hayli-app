package app.asclepius.model;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}
