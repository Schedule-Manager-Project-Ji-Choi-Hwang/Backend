package backend.schedule.controller;


import backend.schedule.dto.Result;
import backend.schedule.dto.SearchPostCondition;
import backend.schedule.dto.StudyPostDto;
import backend.schedule.entity.StudyPost;
import backend.schedule.service.StudyPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class StudyPostController {

    private final StudyPostService studyPostService;

    /**
     * 스터디 게시글 CRUD, 무한 스크롤
     */
    @GetMapping("/studyboard/post")
    public StudyPostDto studyBoardForm(@RequestBody StudyPostDto postDto) {
        return postDto;
    }

    @PostMapping("/studyboard/post") // 등록 버튼 누르면 post 처리 후 /studyboard/{id} 스터디 게시글로 이동
    public StudyPostDto studyBoardPost(@Validated @RequestBody StudyPostDto studyPostDto, BindingResult bindingResult) {

//        if (bindingResult.hasErrors()) {
//            System.exit(0);
//        } //검증시 오류 제어해주는게 있어야 멈춤

        studyPostService.save(studyPostDto);
        return studyPostDto;
    }

    @GetMapping({"/studyboard/{id}", "/studyboard/{id}/edit"})
    public StudyPostDto studyBoardUpdateForm(@PathVariable Long id) {
        StudyPost findStudyPost = studyPostService.findById(id).get(); //예외 처리가 필요한가 고민

        //중간에 이 게시글을 작성한 사람이 맞는지 확인하는 로직 필요
        StudyPostDto studyPostDto = new StudyPostDto(findStudyPost);

        return studyPostDto;
    }

    @Transactional
    @PatchMapping("/studyboard/{id}/edit") // 업데이트 처리 후 /studyboard/{id} 스터디 게시글로 이동
    public StudyPostDto studyBoardUpdate(@Validated @RequestBody StudyPostDto studyPostDto, BindingResult bindingResult,
                                         @PathVariable Long id) {
        StudyPost findStudyPost = studyPostService.findById(id).get();
        findStudyPost.updatePost(studyPostDto);

        return studyPostDto;
    }

    /**
     * @param lastPostId 마지막 조회 id (처음 조회 시는 null)
     * @param condition  게시글 검색 조건 (게시글 제목)
     */
    @GetMapping("/studyboard") //스터디 게시글 전체 조회
    public Result studyBoardLists(@RequestParam(required = false) Long lastPostId,
                                  @RequestBody SearchPostCondition condition, Pageable pageable) {
        return new Result(studyPostService.search(lastPostId, condition, pageable));
    }

    @DeleteMapping("/studyboard/{id}/delete") //삭제 성공하면 /studyboard 스터디 게시판으로 이동
    public String studyBoardDelete(@PathVariable Long id) {
        studyPostService.delete(id);

        return "삭제되었습니다.";
    }
}
