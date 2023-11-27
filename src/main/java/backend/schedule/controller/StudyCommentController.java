package backend.schedule.controller;

import backend.schedule.dto.MessageReturnDto;
import backend.schedule.dto.Result;
import backend.schedule.dto.studycomment.StudyCommentDto;
import backend.schedule.dto.studycomment.StudyCommentSetDto;
import backend.schedule.entity.Member;
import backend.schedule.entity.StudyAnnouncement;
import backend.schedule.entity.StudyComment;
import backend.schedule.enumlist.ConfirmAuthor;
import backend.schedule.jwt.JwtTokenUtil;
import backend.schedule.service.MemberService;
import backend.schedule.service.StudyAnnouncementService;
import backend.schedule.service.StudyCommentService;
import backend.schedule.service.StudyMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

import static backend.schedule.enumlist.ErrorMessage.*;

@RestController
@RequiredArgsConstructor
public class StudyCommentController {

    private final StudyAnnouncementService studyAnnouncementService;
    private final StudyCommentService studyCommentService;
    private final StudyMemberService studyMemberService;
    private final MemberService memberService;
    @Value("${spring.jwt.secretkey}")
    private String mySecretkey;

    /**
     * 스터디 댓글 추가
     * Query: 2번
     */
    @Transactional
    @PostMapping("/study-announcements/{announcementId}/comment/add")//스터디 공지 댓글 추가
    public ResponseEntity<?> studyCommentPost(@Validated @RequestBody StudyCommentDto commentDto,
                                              BindingResult bindingResult, @PathVariable Long announcementId, HttpServletRequest request) {
        //작성한 사람 정보가 안들어감-해결
        //새로운 문제 스터디 회원인 사람만 글 작성 가능하게-해결
        try {
            StudyAnnouncement findAnnouncement = studyAnnouncementService.findById(announcementId);
            String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION).substring(7);
            String memberLoginId = JwtTokenUtil.getLoginId(accessToken, mySecretkey);
            Member findMember = memberService.getLoginMemberByLoginId(memberLoginId);

            studyMemberService.findByMemberAndStudyPost(findMember.getId(), findAnnouncement.getStudyPost().getId(), ConfirmAuthor.MEMBER);
            //findAnnouncement.getStudyPost().getId() 가져오는 방법은 페치조인이 효율적이긴함

            if (bindingResult.hasErrors()) {
                List<String> errorMessages = bindingResult.getAllErrors().stream()
                        .map(ObjectError::getDefaultMessage)
                        .collect(Collectors.toList());

                return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(errorMessages));
            }

            StudyComment studyComment = new StudyComment(commentDto);
            findAnnouncement.addStudyComment(studyComment);
            findMember.addStudyComments(studyComment);

            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(e.getMessage()));
        }

    }

    /**
     * 스터디 댓글 조회
     * Query: 1번
     */
    @GetMapping("/study-announcements/{announcementId}/comment/{commentId}/edit")
    public ResponseEntity<?> studyCommentUpdateForm(@PathVariable Long commentId) {
        //announcementId 아무거나 입력해도 commentId만 맞으면 불러와짐, 댓글 작성한 사람만 수정가능한 로직 필요, 이미 댓글 작성 시 스터디 멤버 아니면 작성하게 해놨음
        try {
            StudyComment comment = studyCommentService.findById(commentId);

            return ResponseEntity.ok().body(new StudyCommentDto(comment));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(e.getMessage()));
        }
    }

    /**
     * 스터디 댓글 전체 조회
     * Query: Fetch join이용 1번
     */
    @GetMapping("/study-announcements/{announcementId}/comments")
    public ResponseEntity<?> announcementCommentList(@PathVariable Long announcementId) {

        try {
            StudyAnnouncement announcement = studyAnnouncementService.announcementCommentList(announcementId);

            return ResponseEntity.ok().body(new Result(new StudyCommentSetDto(announcement)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(e.getMessage()));
        }

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
        //댓글 작성한 사람만 수정가능한 로직 필요, announcementId 아무거나 입력해도 commentId만 맞으면 수정되는 문제
        try {
            StudyComment comment = studyCommentService.findById(commentId);

            if (bindingResult.hasErrors()) {
                List<String> errorMessages = bindingResult.getAllErrors().stream()
                        .map(ObjectError::getDefaultMessage)
                        .collect(Collectors.toList());

                return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(errorMessages));
            }

            comment.commentUpdate(commentDto);

            return ResponseEntity.ok().build(); //수정 후 다시 스터디 공지로 리다리렉트 되게하기(/studyboard/{boardId})boardId 필요
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(e.getMessage()));
        }

    }

    /**
     * 스터디 댓글 삭제
     * Query: 4번
     */
    @Transactional
    @DeleteMapping("/study-announcements/{announcementId}/comment/{commentId}/delete")
    public ResponseEntity<?> studyCommentDelete(@PathVariable Long announcementId, @PathVariable Long commentId) {
        //댓글 작성한 사람만 삭제가능한 로직 필요
        try {
            StudyAnnouncement findAnnouncement = studyAnnouncementService.findById(announcementId);
            StudyComment comment = studyCommentService.findById(commentId);

            findAnnouncement.removeStudyComment(comment);
            //쿼리 4번 개선방법 생각
            return ResponseEntity.ok().body(new MessageReturnDto().okSuccess(DELETE));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(e.getMessage()));
        }

    }
}
