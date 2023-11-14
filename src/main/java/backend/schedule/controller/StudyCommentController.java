package backend.schedule.controller;

import backend.schedule.dto.Result;
import backend.schedule.dto.StudyCommentDto;
import backend.schedule.dto.StudyCommentSetDto;
import backend.schedule.entity.StudyAnnouncement;
import backend.schedule.entity.StudyComment;
import backend.schedule.service.StudyAnnouncementService;
import backend.schedule.service.StudyCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class StudyCommentController {

    private final StudyAnnouncementService studyAnnouncementService;
    private final StudyCommentService studyCommentService;

    /**
     * 스터디 댓글 CRUD
     */

//    @GetMapping("/study-announcements/{id}/comment/add")
//    public StudyCommentDto studyCommentForm(@RequestBody StudyCommentDto commentDto) {
//        return commentDto;
//    }

    @Transactional
    @PostMapping("/study-announcements/{id}/comment/add")//스터디 공지 댓글 추가
    public ResponseEntity<?> studyCommentPost(@Validated @RequestBody StudyCommentDto commentDto,
                                            BindingResult bindingResult, @PathVariable Long id) {
        StudyAnnouncement findAnnouncement = studyAnnouncementService.findById(id);

        if (findAnnouncement == null) {
            return ResponseEntity.badRequest().body("공지를 찾을 수 없습니다.");
        }

        StudyComment studyComment = new StudyComment(commentDto);
        findAnnouncement.addStudyComment(studyComment);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/study-announcements/{id}/comment/{commentId}/edit")
    public ResponseEntity<?> studyCommentUpdateForm(@PathVariable Long id, @PathVariable Long commentId) {
        StudyComment comment = studyCommentService.findById(id);

        if (comment == null) {
            return ResponseEntity.badRequest().body("댓글을 찾을 수 없습니다.");
        }

        return ResponseEntity.ok().body(new StudyCommentDto(comment));
    }

    @Transactional
    @PatchMapping("/study-announcements/{id}/comment/{commentId}/edit")
    public ResponseEntity<?> studyCommentUpdate(
            @Validated @RequestBody StudyCommentDto commentDto,
            BindingResult bindingResult, @PathVariable Long id, @PathVariable Long commentId) {

        StudyComment comment = studyCommentService.findById(id);

        if (comment == null) {
            return ResponseEntity.badRequest().body("댓글을 찾을 수 없습니다.");
        }

        comment.commentUpdate(commentDto);

        return ResponseEntity.ok().build(); //수정 후 다시 스터디 공지로 리다리렉트 되게하기(/studyboard/{boardId})boardId 필요
    }

    @Transactional
    @DeleteMapping("/study-announcements/{id}/comment/{commentId}/delete")
    public ResponseEntity<?> studyCommentDelete(@PathVariable Long id, @PathVariable Long commentId) {
        StudyAnnouncement findAnnouncement = studyAnnouncementService.findById(id);
        StudyComment comment = studyCommentService.findById(commentId);

        if (findAnnouncement == null) {
            return ResponseEntity.badRequest().body("공지를 찾을 수 없습니다.");
        } else if (comment == null) {
            return ResponseEntity.badRequest().body("댓글을 찾을 수 없습니다.");
        }

        findAnnouncement.removeStudyComment(comment);
        //쿼리 4번 개선방법 생각
        return ResponseEntity.ok().body("삭제되었습니다.");
    }

    @GetMapping("/study-announcements/{id}/comments") //전체 공지 조회
    public Result announcementCommentList(@PathVariable Long id) {
        StudyAnnouncement announcement = studyAnnouncementService.announcementCommentList(id);

        return new Result(new StudyCommentSetDto(announcement));
    }
}
