package backend.schedule.controller;

import backend.schedule.dto.Result;
import backend.schedule.dto.StudyCommentDto;
import backend.schedule.dto.StudyCommentSetDto;
import backend.schedule.entity.StudyAnnouncement;
import backend.schedule.entity.StudyComment;
import backend.schedule.service.StudyAnnouncementService;
import backend.schedule.service.StudyCommentService;
import lombok.RequiredArgsConstructor;
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

    @GetMapping("/study-announcements/{id}/comment/add")
    public StudyCommentDto studyCommentForm(@RequestBody StudyCommentDto commentDto) {
        return commentDto;
    }

    @Transactional
    @PostMapping("/study-announcements/{id}/comment/add")//스터디 공지 댓글 추가
    public StudyCommentDto studyCommentPost(@Validated @RequestBody StudyCommentDto commentDto,
                                            BindingResult bindingResult, @PathVariable Long id) {
        StudyAnnouncement findAnnouncement = studyAnnouncementService.findById(id).get();
        StudyComment studyComment = new StudyComment(commentDto);
        findAnnouncement.addStudyComment(studyComment);
        return commentDto;
    }

    @GetMapping("/study-announcements/{id}/comment/{commentId}/edit")
    public StudyCommentDto studyCommentUpdateForm(@PathVariable Long id, @PathVariable Long commentId) {
        StudyComment comment = studyCommentService.findById(id).get();

        return new StudyCommentDto(comment);
    }

    @Transactional
    @PatchMapping("/study-announcements/{id}/comment/{commentId}/edit")
    public StudyCommentDto studyCommentUpdate(
            @Validated @RequestBody StudyCommentDto commentDto,
            BindingResult bindingResult, @PathVariable Long id, @PathVariable Long commentId) {

        StudyComment comment = studyCommentService.findById(id).get();

        comment.commentUpdate(commentDto);

        return commentDto;
    }

    @Transactional
    @DeleteMapping("/study-announcements/{id}/comment/{commentId}/delete")
    public String studyCommentDelete(@PathVariable Long id, @PathVariable Long commentId) {
        StudyAnnouncement findAnnouncement = studyAnnouncementService.findById(id).get();
        StudyComment studyComment = studyCommentService.findById(commentId).get();

        findAnnouncement.removeStudyComment(studyComment);
        //쿼리 4번 개선방법 생각
        return "삭제되었습니다.";
    }

    @GetMapping("/study-announcements/{id}/comments") //전체 공지 조회
    public Result announcementCommentList(@PathVariable Long id) {
        StudyAnnouncement announcement = studyAnnouncementService.announcementCommentList(id);

        return new Result(new StudyCommentSetDto(announcement));
    }
}
