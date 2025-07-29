package app.asclepius.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class IntercomEventRequest {
    private String eventName;
    private String createdAt;
    private String id;
    private Map<String, String> metadata;
}
