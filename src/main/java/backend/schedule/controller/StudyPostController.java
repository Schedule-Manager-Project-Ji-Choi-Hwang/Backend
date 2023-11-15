package backend.schedule.controller;


import backend.schedule.dto.*;
import backend.schedule.entity.StudyPost;
import backend.schedule.service.StudyPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static backend.schedule.enumlist.ErrorMessage.DELETE;
import static backend.schedule.enumlist.ErrorMessage.POST;

@RestController
@RequiredArgsConstructor
public class StudyPostController {

    private final StudyPostService studyPostService;

    /**
     * 스터디 게시글 CRUD, 무한 스크롤
     */
//    @GetMapping("/studyboard/post")
//    public StudyPostDto studyBoardForm(@RequestBody StudyPostDto postDto) {
//        return postDto;
//    }
    @PostMapping("/studyboard/post") // 등록 버튼 누르면 post 처리 후 /studyboard/{id} 스터디 게시글로 이동
    public ResponseEntity<?> studyBoardPost(@Validated @RequestBody StudyPostDto studyPostDto, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.toList());

            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(errorMessages));
        }

        Long savedPostId = studyPostService.save(studyPostDto);

        return ResponseEntity.ok().body(new ReturnIdDto(savedPostId));
    }

    @GetMapping({"/studyboard/{id}", "/studyboard/{id}/edit"})
    public ResponseEntity<?> studyBoardUpdateForm(@PathVariable Long id) {
        StudyPost findStudyPost = studyPostService.findById(id);

        if (findStudyPost == null) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(POST));
        }

        StudyPostDto studyPostDto = new StudyPostDto(findStudyPost);

        return ResponseEntity.ok().body(studyPostDto);
    }

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
     * @param lastPostId 마지막 조회 id (처음 조회 시는 null)
     * @param condition  게시글 검색 조건 (게시글 제목)
     */
    @GetMapping("/studyboard") //스터디 게시글 전체 조회
    public ResponseEntity<Result> studyBoardLists(@RequestParam(required = false) Long lastPostId,
                                                  @RequestBody SearchPostCondition condition, Pageable pageable) {
        return ResponseEntity.ok().body(new Result(studyPostService.search(lastPostId, condition, pageable)));
    }

    @DeleteMapping("/studyboard/{id}/delete") //삭제 성공하면 /studyboard 스터디 게시판으로 이동
    public ResponseEntity<?> studyBoardDelete(@PathVariable Long id) {
        studyPostService.delete(id);

        return ResponseEntity.ok().body(new MessageReturnDto().okSuccess(DELETE));
    }
}