package app.asclepius.model;

import lombok.Data;

import java.time.Instant;

@Data
public class UserProfileResponse {
    private String email;
    private String firstName;
    private String lastName;
    private Instant createdAt;
    private Instant updatedAt;
}
