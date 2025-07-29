package app.asclepius.repository;

import app.asclepius.entity.MoodEntity;
import app.asclepius.entity.UserEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface MoodRepository extends JpaRepository<MoodEntity, Integer> {
    Page<MoodEntity> findAllByUser(UserEntity userEntity, Pageable pageable);
    Page<MoodEntity> findAllByUserAndMoodDateBetween(UserEntity userEntity,
                                                                    LocalDate moodDateStart,
                                                                    LocalDate moodDateEnd,
                                                                    Pageable pageable);
    Page<MoodEntity> findAllByUserAndMoodDateAfter(UserEntity userEntity, LocalDate moodDateStart, Pageable pageable);
    MoodEntity findByIdAndUser(Integer id, UserEntity user);

    Boolean existsByIdAndUser(Integer id, UserEntity user);

    @Query("select m from MoodEntity m where m.moodDate between :start and :end and date(m.createdAt) = m.moodDate")
    Stream<MoodEntity> findAllAlertDataAsStream(LocalDate start, LocalDate end);

    @Transactional
    default Map<UserEntity, List<MoodEntity>> retrieveAlertProcessingData(LocalDate start, LocalDate end) {
        return findAllAlertDataAsStream(start, end).collect(Collectors.groupingBy(MoodEntity::getUser));
    }
}
