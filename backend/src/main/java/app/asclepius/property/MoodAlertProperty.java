package app.asclepius.property;

import lombok.Data;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@EnableAutoConfiguration
@EnableConfigurationProperties
@Component
@Configuration
@ConfigurationProperties(prefix = "mood-alert")
@Data
public class MoodAlertProperty {
    private String cron;

    // how many days previously till today to return the range of entries
    private String dayRange;

    // the threshold of when to send alert from the average of scores calculated from total score / data size
    private Integer moodScoreMeanThreshold;

    // how many entries to return from the date range
    private Integer dataSize;
}
