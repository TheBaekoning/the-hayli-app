package app.asclepius.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class MoodDto {
    private Integer id;
    private LocalDate moodDate;
    private Integer moodRating;
    private String notes;
}
