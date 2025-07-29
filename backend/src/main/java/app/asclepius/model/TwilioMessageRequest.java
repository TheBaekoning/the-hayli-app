package app.asclepius.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TwilioMessageRequest {
    private String body;
    private String to;
    private String messagingServiceSid;
}
