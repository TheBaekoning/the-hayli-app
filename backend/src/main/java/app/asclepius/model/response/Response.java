package app.asclepius.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Schema
public class Response {
    private Object data;
    private Error error;
    private List<Object> meta;

    public Response(Object data) {
        this.data = data;
    }

    public Response() {

    }
}
