package backend.schedule.controller;

import backend.schedule.dto.MessageReturnDto;
import backend.schedule.dto.studycomment.StudyCommentDto;
import backend.schedule.entity.Member;
import backend.schedule.entity.StudyAnnouncement;
import backend.schedule.entity.StudyComment;
import backend.schedule.jwt.JwtTokenExtraction;
import backend.schedule.service.StudyAnnouncementService;
import backend.schedule.service.StudyCommentService;
import backend.schedule.service.StudyMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import static backend.schedule.validation.RequestDataValidation.beanValidation;

@RestController
@RequiredArgsConstructor
public class StudyCommentController {

    private final StudyMemberService studyMemberService;
    private final JwtTokenExtraction jwtTokenExtraction;
    private final StudyCommentService studyCommentService;
    private final StudyAnnouncementService studyAnnouncementService;

    @Value("${spring.jwt.secretkey}")
    private String mySecretkey;

    /**
     * 스터디 댓글 추가
     */
    @PostMapping("study-board/{studyBoardId}/study-announcements/{announcementId}/comment/add")
    public ResponseEntity<?> studyCommentPost(@Validated @RequestBody StudyCommentDto studyCommentDto, BindingResult bindingResult,
                                              @PathVariable Long studyBoardId, @PathVariable Long announcementId, HttpServletRequest request) {
        try {
            Member member = jwtTokenExtraction.extractionMember(request, mySecretkey);
            studyMemberService.studyMemberSearchNoAuthority(member.getId(), studyBoardId);

            StudyAnnouncement findAnnouncement = studyAnnouncementService.findStudyAnnouncement(announcementId, studyBoardId);

            if (bindingResult.hasErrors())
                return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(beanValidation(bindingResult)));

            studyCommentService.save(studyCommentDto, findAnnouncement, member);

            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(e.getMessage()));
        }

    }

    /**
     * 스터디 댓글 수정
     */
    @PatchMapping("/study-board/study-announcements/{announcementId}/comment/{commentId}/edit")
    public ResponseEntity<?> studyCommentUpdate(@Validated @RequestBody StudyCommentDto studyCommentDto, BindingResult bindingResult,
                                                @PathVariable Long announcementId, @PathVariable Long commentId, HttpServletRequest request) {
        try {
            Long memberId = jwtTokenExtraction.extractionMemberId(request, mySecretkey);

            StudyComment comment = studyCommentService.writerCheck(announcementId, commentId, memberId);

            if (bindingResult.hasErrors())
                return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(beanValidation(bindingResult)));

            studyCommentService.updateComment(comment, studyCommentDto);

            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(e.getMessage()));
        }

    }

    /**
     * 스터디 댓글 삭제
     */
    @DeleteMapping("/study-announcements/{announcementId}/comment/{commentId}/delete")
    public ResponseEntity<?> studyCommentDelete(@PathVariable Long announcementId, @PathVariable Long commentId, HttpServletRequest request) {
        try {
            Long memberId = jwtTokenExtraction.extractionMemberId(request, mySecretkey);
            String commentRemove = studyCommentService.commentRemove(announcementId, commentId, memberId);

            return ResponseEntity.ok().body(new MessageReturnDto().okSuccess(commentRemove));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(e.getMessage()));
        }

    }
}