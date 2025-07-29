package app.asclepius.service;

import app.asclepius.model.*;

import java.time.LocalDate;
import java.util.List;

public interface MoodService {
    MoodResponse retrieveMoodList(LocalDate start, LocalDate end, Integer page, Integer pageSize, String username, String order);
    MoodResponse retrieveMoodById(Integer moodId, String username);
    List<MoodDto> createMoodRecords(MoodRequest moodRequest, String username);
    void deleteMoodRecord(Integer moodId, String username);
    MoodBulkDeleteResponse deleteMoodRecordsByList(MoodBulkDeleteRequest moodIds, String username);
    MoodDto updateMoodRecord(MoodEntryRequest moodRequest, String name);
}
