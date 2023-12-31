package backend.schedule.controller;


import backend.schedule.dto.MessageReturnDto;
import backend.schedule.dto.Result;
import backend.schedule.dto.ReturnIdDto;
import backend.schedule.dto.studypost.StudyMemberToPostReqDto;
import backend.schedule.dto.studypost.StudyPostDto;
import backend.schedule.dto.studypost.StudyPostNameResDto;
import backend.schedule.dto.studypost.StudyPostResDto;
import backend.schedule.entity.Member;
import backend.schedule.entity.StudyPost;
import backend.schedule.jwt.JwtTokenExtraction;
import backend.schedule.service.ApplicationMemberService;
import backend.schedule.service.StudyMemberService;
import backend.schedule.service.StudyPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static backend.schedule.enumlist.ConfirmAuthor.LEADER;
import static backend.schedule.enumlist.ErrorMessage.DELETE;
import static backend.schedule.validation.RequestDataValidation.beanValidation;

@RestController
@RequiredArgsConstructor
public class StudyPostController {

    private final StudyPostService studyPostService;
    private final JwtTokenExtraction jwtTokenExtraction;
    private final StudyMemberService studyMemberService;
    private final ApplicationMemberService applicationMemberService;

    @Value("${spring.jwt.secretkey}")
    private String mySecretkey;

    /**
     * 스터디 게시글 작성
     */
    @PostMapping("/study-board/post")
    public ResponseEntity<?> studyBoardPost(@Validated @RequestBody StudyPostDto studyPostDto, BindingResult bindingResult, HttpServletRequest request) {
        try {
            Member member = jwtTokenExtraction.extractionMember(request, mySecretkey);

            if (bindingResult.hasErrors())
                return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(beanValidation(bindingResult)));

            studyPostService.save(studyPostDto, member);

            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(e.getMessage()));
        }

    }

    /**
     * 스터디 게시글 조회
     */
    @GetMapping("/study-board/{studyBoardId}")
    public ResponseEntity<?> findStudyBoard(@PathVariable Long studyBoardId, HttpServletRequest request) {
        try {
            Member member = jwtTokenExtraction.extractionMember(request, mySecretkey);
            StudyPost findStudyPost = studyPostService.findById(studyBoardId);
            boolean myAuthority = studyMemberService.myAuthority(member, findStudyPost, LEADER);
            boolean myApplication = applicationMemberService.applicationButtonCheck(member.getId(), studyBoardId);
            boolean myStudyMember = applicationMemberService.studyMemberButtonCheck(member.getId(), studyBoardId);

            return ResponseEntity.ok().body(new StudyPostResDto(findStudyPost, myAuthority, myApplication, myStudyMember));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(e.getMessage()));
        }

    }

    /**
     * 내 스터디 이름 조회
     */
    @GetMapping("/my-study-board/name-list")
    public ResponseEntity<?> myStudyNameList(HttpServletRequest request) {
        try {
            Long memberId = jwtTokenExtraction.extractionMemberId(request, mySecretkey);
            List<StudyPostNameResDto> studyPostNameResDtos = studyMemberService.myPostNameList(memberId);

            return ResponseEntity.ok().body(new Result(studyPostNameResDtos));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(e.getMessage()));
        }
    }

    /**
     * 스터디 게시글 전체 조회 (멤버별)
     */
    @GetMapping("/my-study-board")
    public ResponseEntity<?> myStudies(HttpServletRequest request) {
        try {
            Long memberId = jwtTokenExtraction.extractionMemberId(request, mySecretkey);
            List<StudyMemberToPostReqDto> studyMemberToPostReqDtos = studyMemberService.memberPostList(memberId);

            return ResponseEntity.ok().body(new Result(studyMemberToPostReqDtos));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(e.getMessage()));
        }
    }

    /**
     * 내가 작성한 스터디 게시글 조회
     */
    @GetMapping("/my-postlist")
    public ResponseEntity<?> myPostList(HttpServletRequest request) {
        try {
            Long memberId = jwtTokenExtraction.extractionMemberId(request, mySecretkey);
            List<StudyMemberToPostReqDto> studyMemberToPostReqDtos = studyMemberService.myPostList(memberId);

            return ResponseEntity.ok().body(new Result(studyMemberToPostReqDtos));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(e.getMessage()));
        }

    }

    /**
     * 무한 스크롤
     *
     * @param lastPostId 마지막 조회 id (처음 조회 시는 null)
     * @param studyName  게시글 검색 조건 (게시글 제목)
     */
    @GetMapping("/study-board") //스터디 게시글 전체 조회
    public ResponseEntity<Result> studyBoardLists(@RequestParam(required = false) Long lastPostId,
                                                  @RequestParam(required = false) String studyName, Pageable pageable) {
        return ResponseEntity.ok().body(new Result(studyPostService.search(lastPostId, studyName, pageable)));
    }

    /**
     * 스터디 게시글 수정
     * Query: 2번
     */
    @PatchMapping("/study-board/{studyBoardId}/edit")
    public ResponseEntity<?> studyBoardUpdate(@Validated @RequestBody StudyPostDto studyPostDto, BindingResult bindingResult,
                                              @PathVariable Long studyBoardId, HttpServletRequest request) {
        try {
            Long memberId = jwtTokenExtraction.extractionMemberId(request, mySecretkey);
            studyMemberService.studyMemberSearch(memberId, studyBoardId, LEADER);
            StudyPost findStudyPost = studyPostService.findById(studyBoardId);

            if (bindingResult.hasErrors())
                return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(beanValidation(bindingResult)));

            studyPostService.updateStudyPost(findStudyPost, studyPostDto);

            return ResponseEntity.ok().body(new ReturnIdDto(studyBoardId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(e.getMessage()));
        }

    }

    /**
     * 스터디 게시글 삭제
     */
    @DeleteMapping("/study-board/{studyBoardId}/delete")
    public ResponseEntity<?> studyBoardDelete(@PathVariable Long studyBoardId, HttpServletRequest request) {
        try {
            Long memberId = jwtTokenExtraction.extractionMemberId(request, mySecretkey);
            studyMemberService.studyMemberSearch(memberId, studyBoardId, LEADER);

            StudyPost findStudyPost = studyPostService.findById(studyBoardId);
            studyPostService.delete(findStudyPost);

            return ResponseEntity.ok().body(new MessageReturnDto().okSuccess(DELETE));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(e.getMessage()));
        }
    }
}
