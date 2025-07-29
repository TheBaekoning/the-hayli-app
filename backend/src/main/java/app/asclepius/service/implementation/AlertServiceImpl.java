package app.asclepius.service.implementation;

import app.asclepius.service.AlertService;
import app.asclepius.service.external.IntercomService;
import app.asclepius.service.external.TwilioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AlertServiceImpl implements AlertService {
    private final IntercomService intercomService;
    private final TwilioService twilioService;

    @Override
    public void process(String uuid) {
        log.info("Sending alert");
        intercomService.sendEvent(uuid);
        alertFriendsFamily(uuid);
    }

    private void alertFriendsFamily(String uuid) {
        log.info("Sending alert to friends and family for UUID: {}", uuid);

        twilioService.sendMessage();
    }
}
