package backend.schedule.controller;


import backend.schedule.dto.*;
import backend.schedule.dto.studypost.SearchPostCondition;
import backend.schedule.dto.studypost.StudyMemberToPostReqDto;
import backend.schedule.dto.studypost.StudyPostDto;
import backend.schedule.entity.Member;
import backend.schedule.entity.StudyMember;
import backend.schedule.entity.StudyPost;
import backend.schedule.enumlist.ConfirmAuthor;
import backend.schedule.jwt.JwtTokenExtraction;
import backend.schedule.jwt.JwtTokenUtil;
import backend.schedule.service.MemberService;
import backend.schedule.service.StudyMemberService;
import backend.schedule.service.StudyPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
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

import static backend.schedule.enumlist.ConfirmAuthor.*;
import static backend.schedule.enumlist.ErrorMessage.*;

@RestController
@RequiredArgsConstructor
public class StudyPostController {

    private final StudyPostService studyPostService;
    private final StudyMemberService studyMemberService;
    private final JwtTokenExtraction jwtTokenExtraction;
    @Value("${spring.jwt.secretkey}")
    private String mySecretkey;

    //StudyPost 멤버별 스터디 과목 볼 수 있게 만들기
    //시용자가 작성한 내 글 볼 수 있게 만들기 -> 내가 작성한 글
    //리더가 스터디 탈퇴할때 다른 멤버들이 있으면 그 멤버들 중 하나에게 리더권한을 넘겨주게 됨 근데 게시글 작성한 사람 식별은 스터디 멤버 테이블에서 리더로 확인하는데 이거 맞나?
    /**
     * 스터디 게시글 작성
     * Query: 3번
     */
    @PostMapping("/studyboard/post") // 등록 버튼 누르면 post 처리 후 /studyboard/{id} 스터디 게시글로 이동
    public ResponseEntity<?> studyBoardPost(@Validated @RequestBody StudyPostDto studyPostDto, BindingResult bindingResult, HttpServletRequest request) {

        try {
            Member member = jwtTokenExtraction.extractionMember(request, mySecretkey);

            if (bindingResult.hasErrors()) {
                List<String> errorMessages = bindingResult.getAllErrors().stream()
                        .map(ObjectError::getDefaultMessage)
                        .collect(Collectors.toList());

                return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(errorMessages));
            }

            studyPostService.save(studyPostDto, member);

            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(e.getMessage()));
        }

    }

    /**
     * 스터디 게시글 조회
     * Query: 1번
     */
    @GetMapping("/studyboard/{studyBoardId}")
    public ResponseEntity<?> findStudyBoard(@PathVariable Long studyBoardId) {

        try {
            StudyPost findStudyPost = studyPostService.findById(studyBoardId);

            return ResponseEntity.ok().body(new StudyPostDto(findStudyPost));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(e.getMessage()));
        }

    }

    /**
     * 스터디 게시글 수정 조회
     * Query: 1번
     */
    @GetMapping("/studyboard/{studyBoardId}/edit")
    public ResponseEntity<?> studyBoardUpdateForm(@PathVariable Long studyBoardId, HttpServletRequest request) {

        try {
            Long memberId = jwtTokenExtraction.extractionMemberId(request, mySecretkey);
            studyMemberService.studyMemberSearch(memberId, studyBoardId, LEADER);
            StudyPost findStudyPost = studyPostService.findById(studyBoardId);

            return ResponseEntity.ok().body(new StudyPostDto(findStudyPost));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(e.getMessage()));
        }

    }

    /**
     * 내가 작성한 스터디 게시글 조회
     * Query: 1번
     */
    @GetMapping("/myPostList")
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
     * @param lastPostId 마지막 조회 id (처음 조회 시는 null)
     * @param condition  게시글 검색 조건 (게시글 제목)
     * Query: Query Dsl이용 1번
     */
    @GetMapping("/studyboard") //스터디 게시글 전체 조회
    public ResponseEntity<Result> studyBoardLists(@RequestParam(required = false) Long lastPostId,
                                                  @RequestParam(required = false) String studyName, Pageable pageable) { //검색어 제목입력받는거 쿼리파라미터 형식으로 바꾸기
        return ResponseEntity.ok().body(new Result(studyPostService.search(lastPostId, studyName, pageable)));
    }

    /**
     * 스터디 게시글 수정
     * Query: 2번
     */
    @PatchMapping("/studyboard/{studyBoardId}/edit") // 업데이트 처리 후 /studyboard/{id} 스터디 게시글로 이동
    public ResponseEntity<?> studyBoardUpdate(@Validated @RequestBody StudyPostDto studyPostDto, BindingResult bindingResult,
                                              @PathVariable Long studyBoardId, HttpServletRequest request) {

        try {
            Long memberId = jwtTokenExtraction.extractionMemberId(request, mySecretkey);
            studyMemberService.studyMemberSearch(memberId, studyBoardId, LEADER);
            StudyPost findStudyPost = studyPostService.findById(studyBoardId);

            if (bindingResult.hasErrors()) {
                List<String> errorMessages = bindingResult.getAllErrors().stream()
                        .map(ObjectError::getDefaultMessage)
                        .collect(Collectors.toList());

                return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(errorMessages));
            }

            studyPostService.updateStudyPost(findStudyPost, studyPostDto);

            return ResponseEntity.ok().body(new ReturnIdDto(studyBoardId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(e.getMessage()));
        }

    }

    /**
     * 스터디 게시글 삭제
     * Query: 3번
     */
    @DeleteMapping("/studyboard/{studyBoardId}/delete") //삭제 성공하면 /studyboard 스터디 게시판으로 이동
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
