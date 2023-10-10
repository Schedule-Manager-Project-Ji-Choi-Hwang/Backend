package backend.schedule.service;


import backend.schedule.entity.StudyPost;
import backend.schedule.repository.StudyPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StudyPostService {

    private final StudyPostRepository studyPostRepository;

    public void save(StudyPost studyPost) {
        studyPostRepository.save(studyPost);
    }

    public Optional<StudyPost> findById(Long id) {
        return studyPostRepository.findById(id);
    }

    public void delete(StudyPost studyPost) {
        studyPostRepository.delete(studyPost);
    }
}
