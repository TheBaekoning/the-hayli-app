package app.asclepius.scheduler;

import app.asclepius.entity.MoodEntity;
import app.asclepius.entity.UserEntity;
import app.asclepius.property.MoodAlertProperty;
import app.asclepius.repository.MoodRepository;
import app.asclepius.service.AlertService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class AlertScheduler {
    private final MoodRepository moodRepository;
    private final MoodAlertProperty moodAlertProperty;
    private final AlertService alertService;
    @Scheduled(cron = "${mood-alert.cron}")
    public void run(){
        log.info("---- Starting mood-alert scheduled cron job ----");

        // retrieve data to process where start date is x days before today to today
        Map<UserEntity, List<MoodEntity>> mappedData = moodRepository
                .retrieveAlertProcessingData(LocalDate.now().minusDays(Long.parseLong(moodAlertProperty.getDayRange())), LocalDate.now());

        mappedData.forEach((key, value) -> {
            if (value.size() != moodAlertProperty.getDataSize()) {
                return;
            }

            Double moodRatingAverage = value.stream().collect(Collectors.averagingInt(MoodEntity::getMoodRating));

            if (moodRatingAverage < moodAlertProperty.getMoodScoreMeanThreshold()) {
                alert(key.getUuid());
            }
        });
    }

    private void alert(String uuid) {
        log.info("Sending alert! I has fallen and cannot get up!");
        alertService.process(uuid);
    }
}
