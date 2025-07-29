package app.asclepius.mapper;

import app.asclepius.entity.UserProfileEntity;
import app.asclepius.model.UserProfileRequest;
import app.asclepius.model.UserProfileResponse;
import org.mapstruct.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserProfileMapper {
    UserProfileResponse userProfileEntityToUserProfileResponse(UserProfileEntity entity);

    @Mapping(target = "users", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    UserProfileEntity userProfileRequestToUserProfileEntity(UserProfileRequest request);

    @Mapping(target = "users", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    void updateEntityWithRequest(UserProfileRequest request, @MappingTarget UserProfileEntity userProfileEntity);
}
