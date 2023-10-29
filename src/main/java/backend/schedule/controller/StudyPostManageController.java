package backend.schedule.controller;


import backend.schedule.dto.*;
import backend.schedule.entity.StudyAnnouncement;
import backend.schedule.entity.StudyPost;
import backend.schedule.entity.StudySchedule;
import backend.schedule.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@Slf4j
public class StudyPostManageController {

    private final StudyPostService studyPostService;
    private final StudyMemberService studyMemberService;
    private final ApplicationMemberService applicationMemberService;
    private final StudyAnnouncementService studyAnnouncementService;
    private final StudyScheduleService studyScheduleService;

    /**
     * 스터디 게시글 관련
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
    @PostMapping("/studyboard/{id}/edit") // 업데이트 처리 후 /studyboard/{id} 스터디 게시글로 이동
    public StudyPostDto studyBoardUpdate(@Validated @RequestBody StudyPostDto studyPostDto, BindingResult bindingResult,
                                         @PathVariable Long id) {
        StudyPost findStudyPost = studyPostService.findById(id).get();
        findStudyPost.updatePost(studyPostDto);

        return studyPostDto;
    }

//    @GetMapping("/studyboard") //스터디 게시글 전체 조회
//    public Result studyBoardList(Pageable pageable) {
//        return new Result(studyPostService.findAll(pageable).map(StudyPostDto::new));
//    }

    /**
     * @param lastPostId 마지막 조회 id (처음 조회 시는 null)
     * @param condition 게시글 검색 조건 (게시글 제목)
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

    /**
     * 스터디 일정 관련
     */
    @GetMapping("/studyboard/{boardId}/study-schedule/add")
    public StudyScheduleDto studyScheduleForm(@RequestBody StudyScheduleDto scheduleDto) {
        return scheduleDto;
    }

    @Transactional
    @PostMapping("/studyboard/{boardId}/study-schedule/add")
    public StudyScheduleDto studyScheduleAdd(@Validated @RequestBody StudyScheduleDto scheduleDto, BindingResult bindingResult, @PathVariable Long boardId) {
        StudyPost findPost = studyPostService.findById(boardId).get();

//        StudySchedule studySchedule = studyScheduleService.save(scheduleDto);
        StudySchedule studySchedule = new StudySchedule(scheduleDto);
        findPost.addStudySchedule(studySchedule); //편의 메서드
        //쿼리 3번나감 개선방법 생각
        return scheduleDto;
    }

    @GetMapping("/studyboard/{boardId}/study-schedule/{id}/edit")
    public StudyScheduleDto studyScheduleUpdateForm(@PathVariable Long id) {
        StudySchedule findSchedule = studyScheduleService.findById(id).get();

        StudyScheduleDto studyScheduleDto = new StudyScheduleDto(findSchedule);

        return studyScheduleDto;
    }

    @Transactional
    @PostMapping("/studyboard/{boardId}/study-schedule/{id}/edit")
    public StudyScheduleDto studyScheduleUpdate(
            @Validated @RequestBody StudyScheduleDto scheduleDto, BindingResult bindingResult,
            @PathVariable Long id, @PathVariable Long boardId) {

        StudySchedule findSchedule = studyScheduleService.findById(id).get();

        findSchedule.updateSchedule(scheduleDto.getScheduleName(), scheduleDto.getPeriod());

        return scheduleDto;
    }

    @Transactional
    @DeleteMapping("/studyboard/{boardId}/study-schedule/{id}/delete")
    public String studyScheduleDelete(@PathVariable Long boardId, @PathVariable Long id) {
        StudyPost findPost = studyPostService.findById(boardId).get();
        StudySchedule findSchedule = studyScheduleService.findById(id).get();

//        studyScheduleService.delete(findSchedule);
        findPost.removeStudySchedule(findSchedule);
        //쿼리 4번 개선방법 생각
        return "삭제되었습니다.";
    }

    @GetMapping("/studyboard/{boardId}/study-schedules")
    public Result studyScheduleFind(@PathVariable Long boardId) {
        StudyPost studyPost = studyPostService.studyScheduleList(boardId);

        return new Result(new StudyPostScheduleSetDto(studyPost));
    }

    /**
     * 스터디 공지사항 관련
     */
    @GetMapping("/studyboard/{boardId}/study-announcements/add")
    public StudyAnnouncementDto studyAnnouncementForm(@RequestBody StudyAnnouncementDto announcementDto) {
        return announcementDto;
    }

    @Transactional
    @PostMapping("/studyboard/{boardId}/study-announcements/add")//스터디 공지 추가
    public StudyAnnouncementDto studyAnnouncementPost(@Validated @RequestBody StudyAnnouncementDto announcementDto,
                                                      BindingResult bindingResult, @PathVariable Long boardId) {
        StudyPost findPost = studyPostService.findById(boardId).get();
        StudyAnnouncement announcement = new StudyAnnouncement(announcementDto);
//        StudyAnnouncement studyAnnouncement = studyAnnouncementService.save(announcementDto);
        findPost.addStudyAnnouncements(announcement); //이 편의 메서드 때문에 update쿼리 한번 더 나감
                                                      //쿼리 총 3번 cascade로 더티체킹하면 자동안될라나?
                                                      //된다
        return announcementDto;
    }

    @GetMapping("/studyboard/{boardId}/study-announcements/{id}/edit")
    public StudyAnnouncementDto studyAnnouncementUpdateForm(@PathVariable Long id, @PathVariable Long boardId) {
        StudyAnnouncement announcement = studyAnnouncementService.findById(id).get();

        return new StudyAnnouncementDto(announcement);
    }

    @Transactional
    @PatchMapping("/studyboard/{boardId}/study-announcements/{id}/edit")
    public StudyAnnouncementDto studyAnnouncementUpdate(
            @Validated @RequestBody StudyAnnouncementDto announcementDto,
            BindingResult bindingResult, @PathVariable Long id, @PathVariable Long boardId) {

        StudyAnnouncement announcement = studyAnnouncementService.findById(id).get();
        announcement.announcementUpdate(announcementDto);

        return announcementDto;
    }

    @Transactional
    @DeleteMapping("/studyboard/{boardId}/study-announcements/{id}/delete")
    public String studyAnnouncementDelete(@PathVariable Long boardId, @PathVariable Long id) {
        StudyPost findPost = studyPostService.findById(boardId).get();
        StudyAnnouncement announcement = studyAnnouncementService.findById(id).get();

        findPost.removeStudyAnnouncement(announcement);
        //쿼리 4번 개선방법 생각
        return "삭제되었습니다.";
    }












































}
