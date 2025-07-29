package app.asclepius.service;

import app.asclepius.entity.UserEntity;
import app.asclepius.model.*;

public interface UserService {
    UserResponse retrieveUserById(Integer userId);

    UserResponse retrieveByEmail(String email);

    UserDto retrieveUserByUuid(String username);

    UserEntity createUser(String password, String email);

    UserResponse modifyUser(Integer userId, UserRequest userRequest);

    UserProfileResponse retrieveUserProfileById (Integer userId);

    void createUserProfile(UserEntity user, String email);

    UserProfileResponse createUserProfile(Integer userId, UserProfileRequest userProfileRequest);

    UserProfileResponse modifyUserProfile(Integer userId, UserProfileRequest userProfileRequest);

}
