package backend.schedule.controller;

import backend.schedule.dto.Result;
import backend.schedule.dto.StudyAnnouncementDto;
import backend.schedule.dto.StudyAnnouncementSetDto;
import backend.schedule.entity.StudyAnnouncement;
import backend.schedule.entity.StudyPost;
import backend.schedule.service.StudyAnnouncementService;
import backend.schedule.service.StudyPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class StudyAnnouncementController {

    private final StudyPostService studyPostService;
    private final StudyAnnouncementService studyAnnouncementService;

    /**
     * 스터디 공지사항 CRUD
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

    @GetMapping("/studyboard/{boardId}/study-announcements/{id}") //공지 단건 조회
    public Result studyAnnouncement(@PathVariable Long boardId, @PathVariable Long id) {
        StudyPost studyPost = studyPostService.studyAnnouncement(boardId, id);

        return new Result(new StudyAnnouncementSetDto(studyPost));
    }

    @GetMapping("/studyboard/{boardId}/study-announcements") //전체 공지 조회
    public Result studyAnnouncementList(@PathVariable Long boardId) {
        StudyPost studyPost = studyPostService.studyAnnouncements(boardId);

        return new Result(new StudyAnnouncementSetDto(studyPost));
    }
}
