package backend.schedule.service;


import backend.schedule.entity.StudyComment;
import backend.schedule.enumlist.ErrorMessage;
import backend.schedule.repository.StudyCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StudyCommentService {

    private final StudyCommentRepository studyCommentRepository;

    public StudyComment findById(Long id) {
        Optional<StudyComment> optionalStudyComment = studyCommentRepository.findById(id);

        return optionalStudyComment.orElseThrow(() -> new IllegalArgumentException(ErrorMessage.COMMENT));
    }

    public StudyComment writerCheck(Long studyAnnouncementId, Long studyCommentId, Long memberId) {
        Optional<StudyComment> optionalStudyComment = studyCommentRepository.findByStudyAnnouncementIdAndIdAndMemberId(studyAnnouncementId, studyCommentId, memberId);

        return optionalStudyComment.orElseThrow(() -> new IllegalArgumentException(ErrorMessage.AUTHORITY));
    }

    public void commentRemove(Long studyAnnouncementId, Long studyCommentId, Long memberId) {
        studyCommentRepository.commentRemove(studyAnnouncementId, studyCommentId, memberId);
    }
}
