package app.asclepius.model;

import lombok.Data;

@Data
public class UserProfileRequest {
    private String firstName;
    private String lastName;
    private String email;
}
