package app.asclepius.service.external.implementation;

import app.asclepius.model.IntercomEventRequest;
import app.asclepius.property.IntercomProperty;
import app.asclepius.service.external.IntercomService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class IntercomServiceImpl implements IntercomService {
    private final WebClient webClient;
    private final IntercomProperty intercomProperty;

    private HttpHeaders headers = null;

    @PostConstruct
    private void postConstruct() {
        headers = new HttpHeaders();

        headers.add("Authorization", "Bearer " + intercomProperty.getAccessToken());
        headers.add("Content-Type", "application/json");
        headers.add("Intercom-Version", intercomProperty.getVersion());
    }

    @Override
    public void sendEvent(String uuid) {
        IntercomEventRequest eventRequest = createEventRequest(uuid);

        webClient
                .post()
                .uri(intercomEventUri())
                .headers(httpHeaders -> httpHeaders.addAll(headers))
                .body(eventRequest, IntercomEventRequest.class)
                .retrieve()
                .bodyToMono(Void.class)
                .doOnError(error -> log.info("Issue with sending intervention event for user: {}", uuid))
                .subscribe();
    }

    private URI intercomEventUri() {
        return UriComponentsBuilder.fromHttpUrl(intercomProperty.getUrl()).path("/events").build().toUri();
    }

    private IntercomEventRequest createEventRequest(String uuid) {
        IntercomEventRequest request = new IntercomEventRequest();
        Map<String, String> metaData = new HashMap<>();

        metaData.put("Alert Date", LocalDateTime.now().toString());

        request.setEventName("Alert - Intervention Needed");
        request.setId(uuid);
        request.setCreatedAt(LocalDateTime.now().toString());
        request.setMetadata(metaData);

        return request;
    }
}
