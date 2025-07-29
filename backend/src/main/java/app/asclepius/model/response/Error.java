package app.asclepius.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema
public class Error {
    private String message;

    public Error(String message) {
        this.message = message;
    }
}
