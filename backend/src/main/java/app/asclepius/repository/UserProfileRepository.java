package app.asclepius.repository;

import app.asclepius.entity.UserProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfileEntity, Integer> {
    Optional<UserProfileEntity> findByEmail(String email);
    boolean existsByEmail(String email);
}
