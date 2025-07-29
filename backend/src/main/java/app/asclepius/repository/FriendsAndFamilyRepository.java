package app.asclepius.repository;

import app.asclepius.entity.FriendsAndFamilyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FriendsAndFamilyRepository extends JpaRepository<FriendsAndFamilyEntity, Long> {
    List<FriendsAndFamilyEntity> findAllByUserId(Integer userId);

}
