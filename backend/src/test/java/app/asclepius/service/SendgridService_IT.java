package app.asclepius.service;

import app.asclepius.service.external.implementation.SendGridServiceImpl;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
class SendgridService_IT {

    @Autowired
    private SendGridServiceImpl sendGridService;

    @Test
    @Tag("IntegrationTest")
    void sendTestEmail() throws IOException {
        sendGridService.sendVerificationEmail("doug@haylihealth.com", "1234TEST", "asdf");
    }
}
