package backend.schedule.controller;

import backend.schedule.dto.MessageReturnDto;
import backend.schedule.dto.Result;
import backend.schedule.dto.studycomment.StudyCommentDto;
import backend.schedule.dto.studycomment.StudyCommentSetDto;
import backend.schedule.entity.Member;
import backend.schedule.entity.StudyAnnouncement;
import backend.schedule.entity.StudyComment;
import backend.schedule.enumlist.ConfirmAuthor;
import backend.schedule.jwt.JwtTokenExtraction;
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
    @PostMapping("studyboard/{studyBoardId}/study-announcements/{announcementId}/comment/add")//스터디 공지 댓글 추가
    public ResponseEntity<?> studyCommentPost(@Validated @RequestBody StudyCommentDto studyCommentDto, BindingResult bindingResult,
                                              @PathVariable Long studyBoardId, @PathVariable Long announcementId, HttpServletRequest request) {

        try {
            Member member = jwtTokenExtraction.extractionMember(request, mySecretkey);
            studyMemberService.studyMemberSearch(member.getId(), studyBoardId, ConfirmAuthor.MEMBER);
            //findAnnouncement.getStudyPost().getId() 가져오는 방법은 페치조인이 효율적이긴함
            //아니면 도메인에 게시글 PathVariable 추가하던가 - 채택

            StudyAnnouncement findAnnouncement = studyAnnouncementService.findById(announcementId);

            if (bindingResult.hasErrors()) {
                List<String> errorMessages = bindingResult.getAllErrors().stream()
                        .map(ObjectError::getDefaultMessage)
                        .collect(Collectors.toList());

                return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(errorMessages));
            }

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
    @GetMapping("/study-announcements/{announcementId}/comment/{commentId}/edit")
    public ResponseEntity<?> studyCommentUpdateForm(@PathVariable Long announcementId, @PathVariable Long commentId, HttpServletRequest request) {
        //스터디 멤버 확인하는 로직 없는 이유는 댓글 작성했을때 확인했으니 굳이 할필요 있나해서 없음
        try {
            Long memberId = jwtTokenExtraction.extractionMemberId(request, mySecretkey);

            StudyComment comment = studyCommentService.writerCheck(announcementId, commentId, memberId); //작성한 사람 맞는지 확인

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
        //검증 필요한가 고민
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
//    @Transactional
    @PatchMapping("/study-announcements/{announcementId}/comment/{commentId}/edit")
    public ResponseEntity<?> studyCommentUpdate(
            @Validated @RequestBody StudyCommentDto studyCommentDto, BindingResult bindingResult,
            @PathVariable Long announcementId, @PathVariable Long commentId, HttpServletRequest request) {

        try {
            Long memberId = jwtTokenExtraction.extractionMemberId(request, mySecretkey);

            StudyComment comment = studyCommentService.writerCheck(announcementId, commentId, memberId);

            if (bindingResult.hasErrors()) {
                List<String> errorMessages = bindingResult.getAllErrors().stream()
                        .map(ObjectError::getDefaultMessage)
                        .collect(Collectors.toList());

                return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(errorMessages));
            }

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
        //댓글 작성한 사람만 삭제가능한 로직 필요
        try {
//            StudyAnnouncement findAnnouncement = studyAnnouncementService.findById(announcementId);
//            StudyComment comment = studyCommentService.findById(commentId); // 어짜피 commentRemove에서 없으면 삭제 안될텐데 있을필요가?
            Long memberId = jwtTokenExtraction.extractionMemberId(request, mySecretkey);

            String commentRemove = studyCommentService.commentRemove(announcementId, commentId, memberId);

            return ResponseEntity.ok().body(new MessageReturnDto().okSuccess(commentRemove));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(e.getMessage()));
        }

    }
}
