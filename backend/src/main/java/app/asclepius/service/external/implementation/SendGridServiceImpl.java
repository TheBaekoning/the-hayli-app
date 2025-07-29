package app.asclepius.service.external.implementation;

import app.asclepius.model.EmailType;
import app.asclepius.property.SendgridProperty;
import app.asclepius.service.external.SendGridService;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Base64;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class SendGridServiceImpl implements SendGridService {
    private static final String SUPPORT_EMAIL = "support@haylihealth.com";
    private static final String CONTENT_TYPE = "text/plain";

    private final SendgridProperty sendgridProperty;

    @Override
    public void userExistsEmail(String email) throws IOException {
        log.info("Sending user exists email.");

        Mail mail = userAlreadyRegisteredMail(email);
        sendMail(mail);
    }

    @Override
    public void sendVerificationEmail(String email, String token, String uuid) throws IOException {
        log.info("Sending verification email for userEmail: {}", email);

        Mail mail = tokenMail(token, email, uuid, EmailType.VERIFY);
        sendMail(mail);
    }

    @Override
    public void sendForgotEmail(String email, String token, String uuid) throws IOException {
        log.info("Sending forgot email for userEmail: {}", email);

        Mail mail = tokenMail(token, email, uuid, EmailType.FORGOT);
        sendMail(mail);
    }

    private void sendMail(Mail mail) throws IOException {
        SendGrid sg = new SendGrid(sendgridProperty.getApiKey());
        Request request = new Request();

        //TODO: Catch when response returns 400 from sendgrid
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            log.info(String.valueOf(response.getStatusCode()));
            log.info(response.getBody());
        } catch (IOException ex) {
            log.error("Unable to process verification email: {}", ex.getMessage());
            throw ex;
        }
    }

    private static Mail tokenMail(String token, String email, String uuid, EmailType type) {
        String urlString = uuid + ":" + token;
        String encoding = Base64.getEncoder().encodeToString(urlString.getBytes());

        if (Objects.requireNonNull(type) == EmailType.VERIFY) {
            return verifyEmail(email, encoding);
        } else if (type == EmailType.FORGOT) {
            return forgotEmail(email, encoding);
        }

        throw new IllegalArgumentException("Incorrect email type.");
    }

    private static Mail forgotEmail (String email, String encoding) {
        String verificationUrl = "https://app.haylihealth.com/forgot/verify?encoding=" + encoding;

        Email from = new Email(SUPPORT_EMAIL);
        String subject = "Account Recovery For Hayli App";
        Email to = new Email(email);
        Content content = new Content(CONTENT_TYPE, "Request new password for Hayli App");
        Mail mail = new Mail(from, subject, to, content);
        mail.setTemplateId("d-ae3cf9ccad384f2b93f0ad95bbc8265f");
        Personalization personalization = mail.getPersonalization().get(0);
        personalization.addDynamicTemplateData("verificationUrlString", verificationUrl);
        mail.addPersonalization(personalization);

        return mail;
    }

    private static Mail verifyEmail (String email, String encoding) {
        String verificationUrl = "https://app.haylihealth.com/sign-up/verify?encoding=" + encoding;

        Email from = new Email(SUPPORT_EMAIL);
        String subject = "Verify Email For Hayli App";
        Email to = new Email(email);
        Content content = new Content(CONTENT_TYPE, "Verify Email for Hayli App");
        Mail mail = new Mail(from, subject, to, content);
        mail.setTemplateId("d-03e71bb7791c4eb1b74bd08a16a21689");
        Personalization personalization = mail.getPersonalization().get(0);
        personalization.addDynamicTemplateData("verificationUrlString", verificationUrl);
        mail.addPersonalization(personalization);

        return mail;
    }

    private static Mail userAlreadyRegisteredMail(String email) {
        Email from = new Email(SUPPORT_EMAIL);
        String subject = "Hey looks like you're already registered!";
        Email to = new Email(email);
        Content content = new Content(CONTENT_TYPE, "Already registered for Hayli App");
        Mail mail = new Mail(from, subject, to, content);
        mail.setTemplateId("d-0ff5149dab5440a2af64825a43b314a6");
        Personalization personalization = mail.getPersonalization().get(0);
        mail.addPersonalization(personalization);
        return mail;
    }
}
