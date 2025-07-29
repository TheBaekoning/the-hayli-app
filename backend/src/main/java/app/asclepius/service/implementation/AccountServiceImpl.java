package app.asclepius.service.implementation;

import app.asclepius.entity.UserEntity;
import app.asclepius.entity.UserProfileEntity;
import app.asclepius.model.NewUserRequest;
import app.asclepius.model.UserResponse;
import app.asclepius.repository.UserProfileRepository;
import app.asclepius.repository.UserRepository;
import app.asclepius.service.AccountService;
import app.asclepius.service.UserService;
import app.asclepius.service.external.SendGridService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Base64;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final UserRepository userRepository;
    private final UserService userService;
    private final SendGridService sendgridService;
    private final UserProfileRepository userProfileRepository;

    @Override
    @Transactional
    public UserResponse userRegistration(NewUserRequest userRequest) throws IOException {
        if (userProfileRepository.existsByEmail(userRequest.getEmail())) {
            // send email that user already exists
            sendgridService.userExistsEmail(userRequest.getEmail());
            return new UserResponse();
        }

        // create user in db
        UserEntity entity = userService.createUser(userRequest.getPassword(), userRequest.getEmail());

        userService.createUserProfile(entity, userRequest.getEmail());

        // send email for verification
        sendVerificationEmail(userRequest.getEmail());
        return new UserResponse();
    }

    @Override
    public void sendVerificationEmail(String email) throws IOException {
        String token = generateRandomToken();
        Optional<UserProfileEntity> userProfileEntity = userProfileRepository.findByEmail(email);
        if (userProfileEntity.isEmpty()) {
            return;
        }
        UserEntity user = userProfileEntity.get().getUsers();

        user.setVerifyToken(token);
        userRepository.save(user);
        userRepository.flush();

        sendgridService.sendVerificationEmail(userProfileEntity.get().getEmail(), token, user.getUuid());
    }

    @Override
    public boolean userVerification(String encoding) {
        byte[] decoded = Base64.getDecoder().decode(encoding);
        String decodedString = new String(decoded);
        String[] delimited = decodedString.split(":");

        assert delimited.length == 2;

        String uuid = delimited[0];
        String token = delimited[1];

        log.info("Decoded verification string with uuid: {}  --  token: {} ", uuid, token);

        UserEntity user = userRepository.findByUuid(uuid);

        if (user == null) {
            return false;
        }

        log.info("Verifying user UUID: {}", uuid);

        if (Objects.equals(user.getVerifyToken(), token)) {
            user.setIsVerified((byte) 1);
            userRepository.save(user);
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public void sentForgotEmail(String email) throws IOException {
        String token = generateRandomToken();
        Optional<UserProfileEntity> userProfileEntity = userProfileRepository.findByEmail(email);
        if (userProfileEntity.isEmpty()) {
            return;
        }
        UserEntity user = userProfileEntity.get().getUsers();

        user.setVerifyToken(token);
        userRepository.save(user);
        userRepository.flush();

        sendgridService.sendForgotEmail(userProfileEntity.get().getEmail(), token, user.getUuid());
    }

    private String generateRandomToken() {
        return RandomStringUtils.randomAlphanumeric(8);
    }
}
