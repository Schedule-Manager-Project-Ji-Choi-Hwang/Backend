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
     * Query: 2번
     */
//    @Transactional
    @PostMapping("study-board/{studyBoardId}/study-announcements/{announcementId}/comment/add")//스터디 공지 댓글 추가
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
     * 스터디 댓글 수정 조회
     * Query: 1번
     */
    @GetMapping("/study-board/study-announcements/{announcementId}/comment/{commentId}/edit")
    public ResponseEntity<?> studyCommentUpdateForm(@PathVariable Long announcementId, @PathVariable Long commentId, HttpServletRequest request) {
        try {
            Long memberId = jwtTokenExtraction.extractionMemberId(request, mySecretkey);

            StudyComment comment = studyCommentService.writerCheck(announcementId, commentId, memberId);

            return ResponseEntity.ok().body(new StudyCommentDto(comment));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(e.getMessage()));
        }
    }

//    /**
//     * 스터디 댓글 전체 조회
//     * Query: Fetch join이용 1번
//     */
//    @GetMapping("/study-board/{studyBoardId}/study-announcements/{announcementId}/comments")
//    public ResponseEntity<?> announcementCommentList(@PathVariable Long studyBoardId, @PathVariable Long announcementId, HttpServletRequest request) {
//        //검증 필요한가 고민
//        try {
//            Long memberId = jwtTokenExtraction.extractionMemberId(request, mySecretkey);
//
//            studyMemberService.studyMemberSearchNoAuthority(memberId, studyBoardId);
//
//            StudyAnnouncement announcement = studyAnnouncementService.announcementCommentList(announcementId, studyBoardId);
//
//            return ResponseEntity.ok().body(new Result(new StudyCommentSetDto(announcement)));
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(e.getMessage()));
//        }
//    }

    /**
     * 스터디 댓글 수정
     * Query: 2번
     */
//    @Transactional
    @PatchMapping("/study-board/study-announcements/{announcementId}/comment/{commentId}/edit")
    public ResponseEntity<?> studyCommentUpdate(@Validated @RequestBody StudyCommentDto studyCommentDto, BindingResult bindingResult,
                                                @PathVariable Long announcementId, @PathVariable Long commentId, HttpServletRequest request) {
        try {
            Long memberId = jwtTokenExtraction.extractionMemberId(request, mySecretkey);

            StudyComment comment = studyCommentService.writerCheck(announcementId, commentId, memberId);

            if (bindingResult.hasErrors())
                return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(beanValidation(bindingResult)));

            studyCommentService.updateComment(comment, studyCommentDto);

            return ResponseEntity.ok().build(); //수정 후 다시 스터디 공지로 리다리렉트 되게하기(/studyboard/{boardId})boardId 필요
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(e.getMessage()));
        }

    }

    /**
     * 스터디 댓글 삭제
     * Query: 4번
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