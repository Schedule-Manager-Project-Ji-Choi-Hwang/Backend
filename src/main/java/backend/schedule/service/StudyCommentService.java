package backend.schedule.service;


import backend.schedule.entity.StudyComment;
import backend.schedule.repository.StudyCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StudyCommentService {

    private final StudyCommentRepository studyCommentRepository;

    public Optional<StudyComment> findById(Long id) {
        return studyCommentRepository.findById(id);
    }
}
