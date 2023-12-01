package backend.schedule.controller;


import backend.schedule.dto.*;
import backend.schedule.dto.studypost.SearchPostCondition;
import backend.schedule.dto.studypost.StudyPostDto;
import backend.schedule.dto.studypost.StudyPostFrontSaveDto;
import backend.schedule.entity.Member;
import backend.schedule.entity.StudyMember;
import backend.schedule.entity.StudyPost;
import backend.schedule.enumlist.ConfirmAuthor;
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

    //StudyPost 멤버별 스터디 과목 볼 수 있게 만들기
    //시용자가 작성한 내 글 볼 수 있게 만들기
    /**
     * 스터디 게시글 작성
     * Query: 3번
     */
    @PostMapping("/studyboard/post") // 등록 버튼 누르면 post 처리 후 /studyboard/{id} 스터디 게시글로 이동
    public ResponseEntity<?> studyBoardPost(@Validated @RequestBody StudyPostDto studyPostDto, BindingResult bindingResult, HttpServletRequest request) {

        try {
            if (bindingResult.hasErrors()) {
                List<String> errorMessages = bindingResult.getAllErrors().stream()
                        .map(ObjectError::getDefaultMessage)
                        .collect(Collectors.toList());

                return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(errorMessages));
            }

            Member findMember = findMemberByToken(request); // 토큰 추출 및 멤버 식별
            StudyPostFrontSaveDto savedStudyPostDto = studyPostService.save(studyPostDto, findMember);

            return ResponseEntity.ok().body(savedStudyPostDto);
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
            StudyPostDto studyPostDto = new StudyPostDto(findStudyPost);

            return ResponseEntity.ok().body(studyPostDto);
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
            Member findMember = findMemberByToken(request); // 토큰 추출 및 멤버 식별
            studyMemberService.studyMemberSearch(findMember.getId(), studyBoardId, ConfirmAuthor.LEADER);
            StudyPost findStudyPost = studyPostService.findById(studyBoardId);

            StudyPostDto studyPostDto = new StudyPostDto(findStudyPost);

            return ResponseEntity.ok().body(studyPostDto);
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
                                                  @RequestBody SearchPostCondition condition, Pageable pageable) {
        return ResponseEntity.ok().body(new Result(studyPostService.search(lastPostId, condition, pageable)));
    }

    /**
     * 스터디 게시글 수정
     * Query: 2번
     */
    @Transactional
    @PatchMapping("/studyboard/{studyBoardId}/edit") // 업데이트 처리 후 /studyboard/{id} 스터디 게시글로 이동
    public ResponseEntity<?> studyBoardUpdate(@Validated @RequestBody StudyPostDto studyPostDto, BindingResult bindingResult,
                                              @PathVariable Long studyBoardId, HttpServletRequest request) {

        try {
            Member findMember = findMemberByToken(request);
            studyMemberService.studyMemberSearch(findMember.getId(), studyBoardId, ConfirmAuthor.LEADER);
            StudyPost findStudyPost = studyPostService.findById(studyBoardId);

            if (bindingResult.hasErrors()) {
                List<String> errorMessages = bindingResult.getAllErrors().stream()
                        .map(ObjectError::getDefaultMessage)
                        .collect(Collectors.toList());

                return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(errorMessages));
            }

            findStudyPost.updatePost(studyPostDto);

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
        Member findMember = findMemberByToken(request);

        if (findMember == null) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(MEMBER));
        }

        StudyMember studyMember = studyMemberService.studyMemberSearch(findMember.getId(), studyBoardId, ConfirmAuthor.LEADER);

        if (studyMember == null) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(STUDY));
        }

        studyPostService.deleteById(studyBoardId);

        return ResponseEntity.ok().body(new MessageReturnDto().okSuccess(DELETE));
        //studyMember의 foreign key 무결성 제약조건에 걸린다 하는데 아마 이거 게시글 지우면 스터디 멤버 관련된 로직도 싹다 지워야할듯

//        try {
//            Member findMember = findMemberByToken(request);
//            studyMemberService.studyMemberSearch(findMember.getId(), studyBoardId);
//
//            studyPostService.delete(studyBoardId);
//
//            return ResponseEntity.ok().body(new MessageReturnDto().okSuccess(DELETE));
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(e.getMessage()));
//        }

    }

    /**
     * 토큰이용 Member찾기
     * @param request
     * @return Member
     */
    private Member findMemberByToken(HttpServletRequest request) {
        String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION).substring(7);
        String memberLoginId = JwtTokenUtil.getLoginId(accessToken, mySecretkey);
        return memberService.getLoginMemberByLoginId(memberLoginId);
    }
}
