package app.asclepius.service.implementation;

import app.asclepius.entity.FriendsAndFamilyEntity;
import app.asclepius.mapper.FriendsAndFamilyMapper;
import app.asclepius.model.ContactDto;
import app.asclepius.model.UserDto;
import app.asclepius.repository.FriendsAndFamilyRepository;
import app.asclepius.service.NotificationService;
import app.asclepius.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final FriendsAndFamilyRepository friendsAndFamilyRepository;
    private final FriendsAndFamilyMapper friendsAndFamilyMapper;
    private final UserService userService;

    @Override
    public List<ContactDto> getFriendsAndFamilies(String username) {
        UserDto userDto = userService.retrieveUserByUuid(username);

        List<FriendsAndFamilyEntity> friendsAndFamilyEntity = friendsAndFamilyRepository.findAllByUserId(userDto.getId());
        return friendsAndFamilyMapper.friendsAndFamilyListToContactDtoList(friendsAndFamilyEntity);
    }
}
