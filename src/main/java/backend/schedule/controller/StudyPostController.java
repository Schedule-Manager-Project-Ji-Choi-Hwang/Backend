package backend.schedule.controller;


import backend.schedule.dto.*;
import backend.schedule.dto.studypost.SearchPostCondition;
import backend.schedule.dto.studypost.StudyPostDto;
import backend.schedule.dto.studypost.StudyPostFrontSaveDto;
import backend.schedule.entity.Member;
import backend.schedule.entity.StudyMember;
import backend.schedule.entity.StudyPost;
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

import static backend.schedule.enumlist.ErrorMessage.*;

@RestController
@RequiredArgsConstructor
public class StudyPostController {

    private final StudyPostService studyPostService;
    private final StudyMemberService studyMemberService;
    private final MemberService memberService;
    @Value("${spring.jwt.secretkey}")
    private String mySecretkey;

    /**
     * 스터디 게시글 CRUD, 무한 스크롤
     */
//    @GetMapping("/studyboard/post")
//    public StudyPostDto studyBoardForm(@RequestBody StudyPostDto postDto) {
//        return postDto;
//    }

    /**
     * 스터디 게시글 작성
     * Query: 3번
     */
    @PostMapping("/studyboard/post") // 등록 버튼 누르면 post 처리 후 /studyboard/{id} 스터디 게시글로 이동
    public ResponseEntity<?> studyBoardPost(@Validated @RequestBody StudyPostDto studyPostDto, BindingResult bindingResult, HttpServletRequest request) {

        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.toList());

            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(errorMessages));
        }

        // 토큰 추출 및 멤버 식별
        String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION).substring(7);
        String memberLoginId = JwtTokenUtil.getLoginId(accessToken, mySecretkey);
        Member findMember = memberService.getLoginMemberByLoginId(memberLoginId);

        if (findMember == null) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(MEMBER));
        }

        StudyPostFrontSaveDto savedStudyPostDto = studyPostService.save(studyPostDto, findMember);

        return ResponseEntity.ok().body(savedStudyPostDto);
    }

    /**
     * 스터디 게시글 조회
     * Query: 1번
     */
    @GetMapping("/studyboard/{id}")
    public ResponseEntity<?> findStudyBoard(@PathVariable Long id) {
        StudyPost findStudyPost = studyPostService.findById(id);

        if (findStudyPost == null) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(POST));
        }

        StudyPostDto studyPostDto = new StudyPostDto(findStudyPost);

        return ResponseEntity.ok().body(studyPostDto);
    }

    /**
     * 스터디 게시글 수정 조회
     * Query: 1번
     */
    @GetMapping("/studyboard/{studyBoardId}/edit")
    public ResponseEntity<?> studyBoardUpdateForm(@PathVariable Long studyBoardId, HttpServletRequest request) {
        String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION).substring(7);
        String memberLoginId = JwtTokenUtil.getLoginId(accessToken, mySecretkey);
        Member findMember = memberService.getLoginMemberByLoginId(memberLoginId);

        if (findMember == null) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(MEMBER));
        }

        StudyMember studyMember = studyMemberService.findByMemberAndStudyPost(findMember.getId(), studyBoardId);

        if (studyMember == null) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(STUDY));
        } //edit, deleted 에도 적용

        StudyPost findStudyPost = studyPostService.findById(studyBoardId);

        if (findStudyPost == null) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(POST));
        }

        StudyPostDto studyPostDto = new StudyPostDto(findStudyPost);

        return ResponseEntity.ok().body(studyPostDto);
    }

    /**
     * @param lastPostId 마지막 조회 id (처음 조회 시는 null)
     * @param condition  게시글 검색 조건 (게시글 제목)
     * Query: Query Dsl이용 1번
     */
    @GetMapping("/studyboard") //스터디 게시글 전체 조회
    public ResponseEntity<Result> studyBoardLists(@RequestParam(required = false) Long lastPostId,
                                                  @RequestBody SearchPostCondition condition, Pageable pageable) {
        return ResponseEntity.ok().body(new Result(studyPostService.search(lastPostId, condition, pageable)));
    }

    /**
     * 스터디 게시글 수정
     * Query: 2번
     */
    @Transactional
    @PatchMapping("/studyboard/{id}/edit") // 업데이트 처리 후 /studyboard/{id} 스터디 게시글로 이동
    public ResponseEntity<?> studyBoardUpdate(@Validated @RequestBody StudyPostDto studyPostDto, BindingResult bindingResult,
                                              @PathVariable Long id) {
        StudyPost findStudyPost = studyPostService.findById(id);

        if (findStudyPost == null) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(POST));
        }

        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.toList());

            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(errorMessages));
        }

        findStudyPost.updatePost(studyPostDto);

        return ResponseEntity.ok().body(new ReturnIdDto(id));
    }

    /**
     * 스터디 게시글 삭제
     * Query: 3번
     */
    @DeleteMapping("/studyboard/{id}/delete") //삭제 성공하면 /studyboard 스터디 게시판으로 이동
    public ResponseEntity<?> studyBoardDelete(@PathVariable Long id) {
        studyPostService.delete(id);

        return ResponseEntity.ok().body(new MessageReturnDto().okSuccess(DELETE));
    }
}
