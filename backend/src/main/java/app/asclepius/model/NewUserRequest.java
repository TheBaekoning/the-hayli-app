package app.asclepius.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class NewUserRequest {
    private String email;
    private String password;
}
