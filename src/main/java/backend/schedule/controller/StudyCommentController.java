package backend.schedule.controller;

import backend.schedule.dto.MessageReturnDto;
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

    @GetMapping("/study-announcements/{id}/comment/{commentId}/edit")
    public ResponseEntity<?> studyCommentUpdateForm(@PathVariable Long id, @PathVariable Long commentId) {
        StudyComment comment = studyCommentService.findById(id);

        if (comment == null) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(COMMENT));
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

    @Transactional
    @DeleteMapping("/study-announcements/{id}/comment/{commentId}/delete")
    public ResponseEntity<?> studyCommentDelete(@PathVariable Long id, @PathVariable Long commentId) {
        StudyAnnouncement findAnnouncement = studyAnnouncementService.findById(id);
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

    @GetMapping("/study-announcements/{id}/comments") //전체 공지 조회
    public Result announcementCommentList(@PathVariable Long id) {
        StudyAnnouncement announcement = studyAnnouncementService.announcementCommentList(id);

        return new Result(new StudyCommentSetDto(announcement));
    }
}