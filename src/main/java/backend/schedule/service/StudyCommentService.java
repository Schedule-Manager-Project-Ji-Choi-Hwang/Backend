package backend.schedule.service;


import backend.schedule.dto.studycomment.StudyCommentDto;
import backend.schedule.entity.Member;
import backend.schedule.entity.StudyAnnouncement;
import backend.schedule.entity.StudyComment;
import backend.schedule.enumlist.ErrorMessage;
import backend.schedule.repository.StudyCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StudyCommentService {

    private final StudyCommentRepository studyCommentRepository;

    @Transactional
    public void save(StudyCommentDto studyCommentDto, StudyAnnouncement studyAnnouncement, Member member) {
        StudyComment studyComment = new StudyComment(studyCommentDto);
        studyAnnouncement.addStudyComment(studyComment);
        member.addStudyComments(studyComment);
        studyCommentRepository.save(studyComment);
    }

    public StudyComment findById(Long id) {
        Optional<StudyComment> optionalStudyComment = studyCommentRepository.findById(id);

        return optionalStudyComment.orElseThrow(() -> new IllegalArgumentException(ErrorMessage.COMMENT));
    }

    public StudyComment writerCheck(Long studyAnnouncementId, Long studyCommentId, Long memberId) {
        Optional<StudyComment> optionalStudyComment = studyCommentRepository.findStudyComment(studyAnnouncementId, studyCommentId, memberId);

        return optionalStudyComment.orElseThrow(() -> new IllegalArgumentException(ErrorMessage.AUTHORITY));
    }

    @Transactional
    public void updateComment(StudyComment studyComment, StudyCommentDto studyCommentDto) {
        studyComment.commentUpdate(studyCommentDto);
    }


    public String commentRemove(Long studyAnnouncementId, Long studyCommentId, Long memberId) {
        int removeComment = studyCommentRepository.commentRemove(studyAnnouncementId, studyCommentId, memberId);

        if (removeComment == 1) {
            return ErrorMessage.DELETE;
        } else {
            throw new IllegalArgumentException(ErrorMessage.NOTDELETE);
        }
    }
}
