package app.asclepius.service.implementation;

import app.asclepius.entity.MoodEntity;
import app.asclepius.entity.UserEntity;
import app.asclepius.mapper.MoodMapper;
import app.asclepius.model.*;
import app.asclepius.repository.MoodRepository;
import app.asclepius.repository.UserRepository;
import app.asclepius.service.MoodService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class MoodServiceImpl implements MoodService {
    private static final Integer MAX_PAGE_SIZE = 100;

    private final MoodRepository moodRepository;
    private final UserRepository userRepository;
    private final MoodMapper moodMapper;

    @Override
    public MoodResponse retrieveMoodList(LocalDate start, LocalDate end, Integer page, Integer pageSize, String username, String order) {
        // sets the pagination configuration based on size, current page (derived from size) and arrange ascending order by moodDate (latest to most recent)
        MoodResponse response = new MoodResponse();

        if (pageSize > MAX_PAGE_SIZE) {
            pageSize = MAX_PAGE_SIZE;
        }

        Pageable pageable;

        if (Objects.equals(order, "ascending")) {
            pageable = PageRequest.of(page, pageSize, Sort.by("moodDate").ascending());
        } else {
            pageable = PageRequest.of(page, pageSize, Sort.by("moodDate").descending());
        }

        UserEntity userEntity = userRepository.findByUuid(username);

        // determines to retrieve paginated data by it's entirety, between two dates or all entries after a date
        Page<MoodEntity> moodPage;

        if (start != null && end != null) {
            moodPage = moodRepository.findAllByUserAndMoodDateBetween(userEntity, start, end, pageable);
        } else if (start != null) {
            moodPage = moodRepository.findAllByUserAndMoodDateAfter(userEntity, start, pageable);
        } else {
            moodPage = moodRepository.findAllByUser(userEntity, pageable);
        }

        List<MoodEntity> moodList = moodPage.getContent();

        response.setMoodEntries(moodMapper.moodEntityListToMoodDtoList(moodList));
        // incrementing page by 1 since pagination data starts from page 1 instead of 0
        response.setCurrentPage(page + 1);
        response.setMaxPages(moodPage.getTotalPages());
        response.setIsLastPage(moodPage.isLast());

        return response;
    }

    @Override
    public MoodResponse retrieveMoodById(Integer moodId, String username) {
        UserEntity userEntity = userRepository.findByUuid(username);
        MoodEntity moodEntity = moodRepository.findByIdAndUser(moodId, userEntity);

        return assignSingleMoodResponse(moodEntity);
    }

    @Override
    public List<MoodDto> createMoodRecords(MoodRequest moodRequest, String username) {
        List<MoodEntity> moodEntities = new ArrayList<>();
        List<MoodDto> moods = moodMapper.moodRequestListToMoodDtoList(moodRequest);

        UserEntity userEntity = userRepository.findByUuid(username);

        for (MoodDto moodDto : moods) {
            MoodEntity moodEntity = moodMapper.moodDtoToMoodEntity(moodDto);
            moodEntity.setUser(userEntity);
            moodEntity.setNotes(moodDto.getNotes());
            moodEntities.add(moodEntity);
        }

        List<MoodEntity> result = moodRepository.saveAllAndFlush(moodEntities);
        return moodMapper.moodEntityListToMoodDtoList(result);
    }

    @Override
    public MoodDto updateMoodRecord(MoodEntryRequest moodRequest, String username) {
        UserEntity userEntity = userRepository.findByUuid(username);

        MoodEntity moodEntity = moodRepository.findByIdAndUser(moodRequest.getId(), userEntity);

        if (moodEntity == null) {
            throw new EntityNotFoundException("Unable to locate mood record");
        }

        MoodDto moodDto = moodMapper.moodEntryRequestToMoodDto(moodRequest);

        moodMapper.updateMoodEntityWithMoodDto(moodDto, moodEntity);

        moodRepository.save(moodEntity);

        return moodMapper.moodEntityToMoodDto(moodEntity);
    }

    @Override
    public void deleteMoodRecord(Integer moodId, String username) {
        UserEntity userEntity = userRepository.findByUuid(username);
        MoodEntity moodEntity = moodRepository.findByIdAndUser(moodId, userEntity);

        moodRepository.delete(moodEntity);
    }

    @Override
    public MoodBulkDeleteResponse deleteMoodRecordsByList(MoodBulkDeleteRequest moodIds, String username) {
        MoodBulkDeleteResponse response = new MoodBulkDeleteResponse();
        response.setDeletedMoodIds(new ArrayList<>());

        UserEntity userEntity = userRepository.findByUuid(username);

        for (Integer moodId: moodIds.getMoodIds()) {
            if (Boolean.TRUE.equals(moodRepository.existsByIdAndUser(moodId, userEntity))) {
                moodRepository.delete(moodRepository.findByIdAndUser(moodId, userEntity));
                response.getDeletedMoodIds().add(moodId);
            }
        }

        return response;
    }

    private MoodResponse assignSingleMoodResponse(MoodEntity moodEntity) {
        MoodResponse response = new MoodResponse();
        response.setMaxPages(1);
        response.setCurrentPage(1);
        response.setIsLastPage(true);
        response.setMoodEntries(new ArrayList<>());
        response.getMoodEntries().add(moodMapper.moodEntityToMoodDto(moodEntity));
        return response;
    }
}
