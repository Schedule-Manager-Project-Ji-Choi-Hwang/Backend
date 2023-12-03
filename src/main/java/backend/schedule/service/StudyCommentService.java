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
        StudyComment studyComment = new StudyComment(studyCommentDto); //member편의 메서드 삭제되면 여기다 멤버 객체입력
        studyAnnouncement.addStudyComment(studyComment);
        member.addStudyComments(studyComment); // 내가 작성한 댓글 목록 구현할거면 필요 아니면 편의 메서드는 없어도 될듯함
        studyCommentRepository.save(studyComment);
    }

    public StudyComment findById(Long id) {
        Optional<StudyComment> optionalStudyComment = studyCommentRepository.findById(id);

        return optionalStudyComment.orElseThrow(() -> new IllegalArgumentException(ErrorMessage.COMMENT));
    }

    public StudyComment writerCheck(Long studyAnnouncementId, Long studyCommentId, Long memberId) {
        Optional<StudyComment> optionalStudyComment = studyCommentRepository.findByStudyAnnouncementIdAndIdAndMemberId(studyAnnouncementId, studyCommentId, memberId);

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
