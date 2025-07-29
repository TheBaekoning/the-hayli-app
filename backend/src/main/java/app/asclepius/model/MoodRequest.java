package app.asclepius.model;

import lombok.Data;

import java.util.List;

@Data
public class MoodRequest {
    private List<MoodEntryRequest> moodEntries;
}
