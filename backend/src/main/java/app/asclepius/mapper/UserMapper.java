package app.asclepius.mapper;

import app.asclepius.entity.UserEntity;
import app.asclepius.model.UserDto;
import app.asclepius.model.UserRequest;
import app.asclepius.model.UserResponse;
import org.mapstruct.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {
    UserResponse userEntityToUserResponse(UserEntity user);

    UserDto userEntityToUserDto(UserEntity user);

    UserResponse userDtoToUserResponse(UserDto userDto);

    @Mapping(target = "verifyToken", ignore = true)
    @Mapping(target = "isVerified", ignore = true)
    UserEntity userDtoToUserEntity(UserDto userDto);

    @Mapping(target = "verifyToken", ignore = true)
    @Mapping(target = "isVerified", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    UserEntity userRequestToUserEntity(UserRequest request);

    @Mapping(target = "verifyToken", ignore = true)
    @Mapping(target = "isVerified", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    void updateEntityWithRequest(UserRequest request, @MappingTarget UserEntity user);
}
