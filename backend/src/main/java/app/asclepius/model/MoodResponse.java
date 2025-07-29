package app.asclepius.model;

import lombok.Data;

import java.util.List;

@Data
public class MoodResponse {
    private Integer maxPages;
    private Integer currentPage;
    private Boolean isLastPage;
    private List<MoodDto> moodEntries;
}
