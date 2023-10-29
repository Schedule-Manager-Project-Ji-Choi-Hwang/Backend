package backend.schedule.service;


import backend.schedule.dto.SearchPostCondition;
import backend.schedule.dto.StudyPostDto;
import backend.schedule.dto.StudyPostResponseDto;
import backend.schedule.entity.StudyPost;
import backend.schedule.repository.StudyPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StudyPostService {

    private final StudyPostRepository studyPostRepository;

    public void save(StudyPostDto studyPostDto) {
        StudyPost studyPost = new StudyPost(studyPostDto);
        studyPostRepository.save(studyPost);
    }

    public Optional<StudyPost> findById(Long id) {
        return studyPostRepository.findById(id);
    }

    public Slice<StudyPost> findAll(Pageable pageable) {
        return studyPostRepository.findAll(pageable);
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
}
