package app.asclepius.service.external.implementation;

import app.asclepius.model.TwilioMessageRequest;
import app.asclepius.property.TwilioProperty;
import app.asclepius.service.external.TwilioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;

@Slf4j
@Component
@RequiredArgsConstructor
public class TwilioServiceImpl implements TwilioService {
    private final WebClient webClient;
    private final TwilioProperty twilioProperty;

    @Override
    public void sendMessage() {
        // TODO implementation
    }

    private void sendSms() {
        TwilioMessageRequest request = new TwilioMessageRequest();

        request.setTo("+17029579141");
        request.setBody("Test Message From Backend");
        request.setMessagingServiceSid("MG0447b969e5d4a07b6c37609d1ad33427");


        Mono<String> body = webClient.post().uri(twilioUri())
                .headers(httpHeaders -> httpHeaders
                        .setBasicAuth(twilioProperty.getUsername(), twilioProperty.getPassword()))
                .bodyValue(request)
                .exchangeToMono(response -> {
                    if (response.statusCode().is2xxSuccessful()) {
                        return response.bodyToMono(String.class);
                    } else {
                        return Mono.error(new RuntimeException("API call failed"));
                    }
                });

        log.info(body.toString());
    }

    private URI twilioUri() {
        return UriComponentsBuilder.fromUriString("https://api.twilio.com/2010-04-01").build().toUri();
    }
}
