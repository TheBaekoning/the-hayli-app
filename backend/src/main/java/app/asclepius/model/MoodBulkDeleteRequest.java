package app.asclepius.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MoodBulkDeleteRequest {
    List<Integer> moodIds;
}
