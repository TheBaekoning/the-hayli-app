package app.asclepius.mapper;

import app.asclepius.entity.FriendsAndFamilyEntity;
import app.asclepius.model.ContactDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface FriendsAndFamilyMapper {
    List<ContactDto> friendsAndFamilyListToContactDtoList(List<FriendsAndFamilyEntity> friendsAndFamilyEntities);

    @Mapping(target = "phone", source = "phoneNumber")
    ContactDto friendsAndFamilyEntityToContactDto(FriendsAndFamilyEntity friendsAndFamilyEntity);
}
