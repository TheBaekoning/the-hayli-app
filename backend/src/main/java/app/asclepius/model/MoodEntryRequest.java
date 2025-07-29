package app.asclepius.model;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class MoodEntryRequest {
    private Integer id;
    private LocalDateTime moodDate;
    private Integer moodRating;
    private String notes;

    public LocalDate getMoodDate() {
        return moodDate.toLocalDate();
    }
}
