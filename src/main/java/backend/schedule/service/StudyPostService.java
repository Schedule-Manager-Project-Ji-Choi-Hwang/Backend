package backend.schedule.service;


import backend.schedule.dto.SearchPostCondition;
import backend.schedule.dto.StudyPostDto;
import backend.schedule.dto.StudyPostResponseDto;
import backend.schedule.dto.StudyPostScheduleSetDto;
import backend.schedule.entity.StudyPost;
import backend.schedule.repository.StudyPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudyPostService {

    private final StudyPostRepository studyPostRepository;

    public StudyPostScheduleSetDto detailStudySchedules(Long studyboardId, LocalDate date) {
        StudyPost studyPost = studyPostRepository.DetailPageStudySchedules(studyboardId, date);
        return new StudyPostScheduleSetDto(studyPost);
    }

    public Long save(StudyPostDto studyPostDto) {
        StudyPost studyPost = new StudyPost(studyPostDto);

        return studyPostRepository.save(studyPost).getId();
    }

    public StudyPost findById(Long id) {
        Optional<StudyPost> optionalStudyPost = studyPostRepository.findById(id);

        return optionalStudyPost.orElse(null);
    }

    public Slice<StudyPostResponseDto> search(Long lastPostId, SearchPostCondition condition, Pageable pageable) {
        return studyPostRepository.searchPost(lastPostId, condition, pageable);
    }

    public void delete(Long id) {
        studyPostRepository.deleteById(id);
    }

    public StudyPost studyScheduleList(Long id) {
        return studyPostRepository.studyScheduleList(id);
    }

    public StudyPost studyAnnouncement(Long boardId, Long id) {
        return studyPostRepository.studyAnnouncement(boardId, id);
    }

    public StudyPost studyAnnouncements(Long boardId) {
        return studyPostRepository.studyAnnouncements(boardId);
    }
}
