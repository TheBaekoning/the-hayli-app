package app.asclepius.service;

import app.asclepius.model.ContactDto;

import java.util.List;

public interface NotificationService {
    List<ContactDto> getFriendsAndFamilies(String username);
}
