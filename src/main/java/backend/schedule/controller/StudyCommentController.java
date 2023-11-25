package backend.schedule.controller;

import backend.schedule.dto.MessageReturnDto;
import backend.schedule.dto.Result;
import backend.schedule.dto.studycomment.StudyCommentDto;
import backend.schedule.dto.studycomment.StudyCommentSetDto;
import backend.schedule.entity.StudyAnnouncement;
import backend.schedule.entity.StudyComment;
import backend.schedule.service.StudyAnnouncementService;
import backend.schedule.service.StudyCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static backend.schedule.enumlist.ErrorMessage.*;

@RestController
@RequiredArgsConstructor
public class StudyCommentController {

    private final StudyAnnouncementService studyAnnouncementService;
    private final StudyCommentService studyCommentService;

    /**
     * 스터디 댓글 추가
     * Query: 2번
     */
    @Transactional
    @PostMapping("/study-announcements/{announcementId}/comment/add")//스터디 공지 댓글 추가
    public ResponseEntity<?> studyCommentPost(@Validated @RequestBody StudyCommentDto commentDto,
                                              BindingResult bindingResult, @PathVariable Long announcementId) {
        StudyAnnouncement findAnnouncement = studyAnnouncementService.findById(announcementId);

        if (findAnnouncement == null) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(POST));
        }

        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.toList());

            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(errorMessages));
        }

        StudyComment studyComment = new StudyComment(commentDto);
        findAnnouncement.addStudyComment(studyComment);

        return ResponseEntity.ok().build();
    }

    /**
     * 스터디 댓글 조회
     * Query: 1번
     */
    @GetMapping("/study-announcements/{announcementId}/comment/{commentId}/edit")
    public ResponseEntity<?> studyCommentUpdateForm(@PathVariable Long commentId) {
        StudyComment comment = studyCommentService.findById(commentId);

        if (comment == null) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(COMMENT));
        }

        return ResponseEntity.ok().body(new StudyCommentDto(comment));
    }

    /**
     * 스터디 댓글 전체 조회
     * Query: Fetch join이용 1번
     */
    @GetMapping("/study-announcements/{announcementId}/comments") //전체 공지 조회
    public Result announcementCommentList(@PathVariable Long announcementId) {
        StudyAnnouncement announcement = studyAnnouncementService.announcementCommentList(announcementId);

        return new Result(new StudyCommentSetDto(announcement));
    }

    /**
     * 스터디 댓글 수정
     * Query: 2번
     */
    @Transactional
    @PatchMapping("/study-announcements/{announcementId}/comment/{commentId}/edit")
    public ResponseEntity<?> studyCommentUpdate(
            @Validated @RequestBody StudyCommentDto commentDto,
            BindingResult bindingResult, @PathVariable Long commentId) {

        StudyComment comment = studyCommentService.findById(commentId);

        if (comment == null) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(COMMENT));
        }

        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.toList());

            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(errorMessages));
        }

        comment.commentUpdate(commentDto);

        return ResponseEntity.ok().build(); //수정 후 다시 스터디 공지로 리다리렉트 되게하기(/studyboard/{boardId})boardId 필요
    }

    /**
     * 스터디 댓글 삭제
     * Query: 4번
     */
    @Transactional
    @DeleteMapping("/study-announcements/{announcementId}/comment/{commentId}/delete")
    public ResponseEntity<?> studyCommentDelete(@PathVariable Long announcementId, @PathVariable Long commentId) {
        StudyAnnouncement findAnnouncement = studyAnnouncementService.findById(announcementId);
        StudyComment comment = studyCommentService.findById(commentId);

        if (findAnnouncement == null) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(POST));
        } else if (comment == null) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(COMMENT));
        }

        findAnnouncement.removeStudyComment(comment);
        //쿼리 4번 개선방법 생각
        return ResponseEntity.ok().body(new MessageReturnDto().okSuccess(DELETE));
    }
}
