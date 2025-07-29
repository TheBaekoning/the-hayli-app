package app.asclepius.mapper;

import app.asclepius.entity.MoodEntity;
import app.asclepius.model.MoodDto;
import app.asclepius.model.MoodEntryRequest;
import app.asclepius.model.MoodRequest;
import org.mapstruct.*;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MoodMapper {
    List<MoodDto> moodEntityListToMoodDtoList(List<MoodEntity> moodEntities);

    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    MoodEntity moodDtoToMoodEntity(MoodDto moodDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateMoodEntityWithMoodDto(MoodDto moodDto, @MappingTarget MoodEntity moodEntity);

    MoodDto moodEntityToMoodDto(MoodEntity moodEntity);

    default List<MoodDto> moodRequestListToMoodDtoList(MoodRequest moodRequest) {
        if (moodRequest.getMoodEntries().isEmpty()) {
            return new ArrayList<>();
        }

        List<MoodDto> result = new ArrayList<>();
        for (MoodEntryRequest moodEntryRequest : moodRequest.getMoodEntries()) {
            MoodDto moodDto = moodEntryRequestToMoodDto(moodEntryRequest);
            result.add(moodDto);
        }

        return result;
    }

    MoodDto moodEntryRequestToMoodDto(MoodEntryRequest moodRequest);
}
