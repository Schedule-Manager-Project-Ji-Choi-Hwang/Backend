package backend.schedule.controller;

import backend.schedule.dto.Result;
import backend.schedule.dto.ReturnIdDto;
import backend.schedule.dto.StudyAnnouncementDto;
import backend.schedule.dto.StudyAnnouncementSetDto;
import backend.schedule.entity.StudyAnnouncement;
import backend.schedule.entity.StudyPost;
import backend.schedule.service.StudyAnnouncementService;
import backend.schedule.service.StudyPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

//    @GetMapping("/studyboard/{boardId}/study-announcements/add")
//    public StudyAnnouncementDto studyAnnouncementForm(@RequestBody StudyAnnouncementDto announcementDto) {
//        return announcementDto;
//    }

    @Transactional
    @PostMapping("/studyboard/{boardId}/study-announcements/add")//스터디 공지 추가
    public ResponseEntity<?> studyAnnouncementPost(@Validated @RequestBody StudyAnnouncementDto announcementDto,
                                                BindingResult bindingResult, @PathVariable Long boardId) {
        StudyPost findPost = studyPostService.findById(boardId);

        if (findPost == null) {
            return ResponseEntity.badRequest().body("게시글을 찾을 수 없습니다.");
        }

        StudyAnnouncement announcement = new StudyAnnouncement(announcementDto);
        findPost.addStudyAnnouncements(announcement);
        Long announcementId = studyAnnouncementService.save(announcement);

        return ResponseEntity.ok().body(announcementId); //dto 반환하기
    }

    @GetMapping("/studyboard/{boardId}/study-announcements/{id}/edit")
    public ResponseEntity<?> studyAnnouncementUpdateForm(@PathVariable Long id, @PathVariable Long boardId) {
        StudyAnnouncement announcement = studyAnnouncementService.findById(id);

        if (announcement == null) {
            return ResponseEntity.badRequest().body("공지를 찾을 수 없습니다.");
        }

        return ResponseEntity.ok().body(new StudyAnnouncementDto(announcement));
    }

    @Transactional
    @PatchMapping("/studyboard/{boardId}/study-announcements/{id}/edit")
    public ResponseEntity<?> studyAnnouncementUpdate(
            @Validated @RequestBody StudyAnnouncementDto announcementDto,
            BindingResult bindingResult, @PathVariable Long boardId, @PathVariable Long id) {

        StudyAnnouncement announcement = studyAnnouncementService.findById(id);

        if (announcement == null) {
            return ResponseEntity.badRequest().body("공지를 찾을 수 없습니다.");
        }

        announcement.announcementUpdate(announcementDto);

        return ResponseEntity.ok().body(new ReturnIdDto(boardId, id));
    }

    @Transactional
    @DeleteMapping("/studyboard/{boardId}/study-announcements/{id}/delete")
    public ResponseEntity<?> studyAnnouncementDelete(@PathVariable Long boardId, @PathVariable Long id) {
        StudyPost findPost = studyPostService.findById(boardId);
        StudyAnnouncement announcement = studyAnnouncementService.findById(id);

        if (findPost == null) {
            return ResponseEntity.badRequest().body("게시글을 찾을 수 없습니다.");
        } else if (announcement == null) {
            return ResponseEntity.badRequest().body("공지를 찾을 수 없습니다.");
        }

        findPost.removeStudyAnnouncement(announcement);
        studyAnnouncementService.delete(id);
        //쿼리 6번 개선방법 생각
        return ResponseEntity.ok().body("삭제되었습니다.");
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
