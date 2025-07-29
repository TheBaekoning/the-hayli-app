package app.asclepius.service.implementation;

import app.asclepius.entity.UserEntity;
import app.asclepius.entity.UserProfileEntity;
import app.asclepius.mapper.UserMapper;
import app.asclepius.mapper.UserProfileMapper;
import app.asclepius.model.*;
import app.asclepius.repository.UserProfileRepository;
import app.asclepius.repository.UserRepository;
import app.asclepius.service.UserService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final UserMapper userMapper;
    private final UserProfileMapper userProfileMapper;

    @Override
    public UserResponse retrieveUserById(Integer userId) {
        Optional<UserEntity> user = userRepository.findById(userId);
        UserResponse response;

        response = user.map(userMapper::userEntityToUserResponse).orElse(new UserResponse());

        return response;
    }

    @Override
    public UserResponse retrieveByEmail(String email) {
        Optional<UserProfileEntity> userProfileEntity = userProfileRepository.findByEmail(email);

        if (userProfileEntity.isEmpty()) {
            throw new EntityNotFoundException();
        }
        Optional<UserEntity> userEntity = Optional.of(userRepository.getReferenceById(userProfileEntity.get().getId()));
        UserResponse response;

        response = userEntity.map(userMapper::userEntityToUserResponse).orElse(new UserResponse());

        return response;
    }

    @Override
    public UserDto retrieveUserByUuid(String uuid) {
        Optional<UserEntity> user = Optional.ofNullable(userRepository.findByUuid(uuid));
        UserDto response;

        response = user.map(userMapper::userEntityToUserDto).orElse(new UserDto());

        return response;
    }

    @Override
    public UserEntity createUser(String password, String email) {
        UserEntity userEntity = new UserEntity();

        userEntity.setPassword(password);
        userEntity.setUuid(UUID.randomUUID().toString());
        userEntity.setIsVerified((byte) 0);

        if (Boolean.TRUE.equals(userProfileRepository.existsByEmail(email))) {
            log.info("User with email: {} -- already exists", email);
            return new UserEntity();
        } else {
            log.info("Creating new user with email: {}", email);
            return userRepository.saveAndFlush(userEntity);
        }
    }

    @Override
    public UserResponse modifyUser(Integer userId, UserRequest userRequest) {
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
        userMapper.updateEntityWithRequest(userRequest, user);
        userRepository.save(user);
        return userMapper.userEntityToUserResponse(user);
    }

    @Override
    public UserProfileResponse retrieveUserProfileById(Integer userId) {
        Optional<UserProfileEntity> userProfileEntity = userProfileRepository.findById(userId);
        UserProfileResponse response;

        response = userProfileEntity.map(userProfileMapper::userProfileEntityToUserProfileResponse).orElse(null);

        return response;
    }

    @Override
    public void createUserProfile(UserEntity user, String email) {
        if (Boolean.TRUE.equals(userProfileRepository.existsById(user.getId()))) {
            throw new EntityExistsException("UUID: " + user.getUuid() + " -- profile already exists");
        } else {
            log.info("Creating new user profile for UUID: {}", user.getUuid());

            UserProfileEntity userProfileEntity = new UserProfileEntity();

            userProfileEntity.setEmail(email);
            userProfileEntity.setUsers(user);

            userProfileRepository.save(userProfileEntity);
        }
    }

    // NOTE: This might get deprecated. 2024.06.09
    @Override
    public UserProfileResponse createUserProfile(Integer userId, UserProfileRequest userProfileRequest) {
        if (Boolean.TRUE.equals(userProfileRepository.existsById(userId))) {
            throw new EntityExistsException("UserId: " + userId + " -- profile already exists");
        } else {
            log.info("Creating new user profile for id: {}", userId);
            UserEntity userEntity = userRepository.findById(userId).orElseThrow(
                    () -> new EntityNotFoundException("UserId: " + userId + "does not exist"));

            UserProfileEntity userProfileEntity = userProfileMapper.userProfileRequestToUserProfileEntity(userProfileRequest);
            userProfileEntity.setId(userProfileEntity.getId());
            userProfileEntity.setUsers(userEntity);

            userProfileRepository.save(userProfileEntity);
            return userProfileMapper.userProfileEntityToUserProfileResponse(userProfileEntity);
        }
    }

    @Override
    public UserProfileResponse modifyUserProfile(Integer userId, UserProfileRequest userProfileRequest) {
        UserProfileEntity userProfileEntity = userProfileRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
        userProfileMapper.updateEntityWithRequest(userProfileRequest, userProfileEntity);
        userProfileRepository.save(userProfileEntity);
        return userProfileMapper.userProfileEntityToUserProfileResponse(userProfileEntity);
    }
}
