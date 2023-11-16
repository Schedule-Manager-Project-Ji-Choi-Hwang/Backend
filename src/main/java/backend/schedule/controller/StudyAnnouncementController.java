package backend.schedule.controller;

import backend.schedule.dto.*;
import backend.schedule.entity.StudyAnnouncement;
import backend.schedule.entity.StudyPost;
import backend.schedule.service.StudyAnnouncementService;
import backend.schedule.service.StudyPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static backend.schedule.enumlist.ErrorMessage.*;

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

    /**
     * 스터디 공지 추가
     * Query: 2번
     */
    @Transactional
    @PostMapping("/studyboard/{boardId}/study-announcements/add")//스터디 공지 추가
    public ResponseEntity<?> studyAnnouncementPost(@Validated @RequestBody StudyAnnouncementDto announcementDto,
                                                   BindingResult bindingResult, @PathVariable Long boardId) {
        StudyPost findPost = studyPostService.findById(boardId);

        if (findPost == null) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(POST));
        }

        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.toList());

            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(errorMessages));
        }

        StudyAnnouncement announcement = new StudyAnnouncement(announcementDto);
        findPost.addStudyAnnouncements(announcement);
        Long announcementId = studyAnnouncementService.save(announcement);

        return ResponseEntity.ok().body(new ReturnIdDto(announcementId));
    }

    /**
     * 스터디 공지 수정 조회?
     * Query: 1번
     */
    @GetMapping("/studyboard/{boardId}/study-announcements/{id}/edit")
    public ResponseEntity<?> studyAnnouncementUpdateForm(@PathVariable Long id, @PathVariable Long boardId) {
        StudyAnnouncement announcement = studyAnnouncementService.findById(id);

        if (announcement == null) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(ANNOUNCEMENT));
        }

        return ResponseEntity.ok().body(new StudyAnnouncementDto(announcement));
    }

    /**
     * 스터디 공지 조회
     * Query: Fetch join이용 1번
     */
    @GetMapping("/studyboard/{boardId}/study-announcements/{id}") //공지 단건 조회
    public Result studyAnnouncement(@PathVariable Long boardId, @PathVariable Long id) {
        StudyPost studyPost = studyPostService.studyAnnouncement(boardId, id);

        return new Result(new StudyAnnouncementSetDto(studyPost));
    }

    /**
     * 스터디 공지 전체 조회
     * Query: Fetch join이용 1번
     */
    @GetMapping("/studyboard/{boardId}/study-announcements") //전체 공지 조회
    public Result studyAnnouncementList(@PathVariable Long boardId) {
        StudyPost studyPost = studyPostService.studyAnnouncements(boardId);

        return new Result(new StudyAnnouncementSetDto(studyPost));
    }

    /**
     * 스터디 공지 수정
     * Query: 2번
     */
    @Transactional
    @PatchMapping("/studyboard/{boardId}/study-announcements/{id}/edit")
    public ResponseEntity<?> studyAnnouncementUpdate(
            @Validated @RequestBody StudyAnnouncementDto announcementDto,
            BindingResult bindingResult, @PathVariable Long boardId, @PathVariable Long id) {

        StudyAnnouncement announcement = studyAnnouncementService.findById(id);

        if (announcement == null) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(ANNOUNCEMENT));
        }

        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.toList());

            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(errorMessages));
        }

        announcement.announcementUpdate(announcementDto);

        return ResponseEntity.ok().body(new ReturnIdDto(boardId, id));
    }

    /**
     * 스터디 공지 삭제
     * Query: 5번
     */
    @Transactional
    @DeleteMapping("/studyboard/{boardId}/study-announcements/{id}/delete")
    public ResponseEntity<?> studyAnnouncementDelete(@PathVariable Long boardId, @PathVariable Long id) {
        StudyPost findPost = studyPostService.findById(boardId);
        StudyAnnouncement announcement = studyAnnouncementService.findById(id);

        if (findPost == null) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(POST));
        } else if (announcement == null) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(ANNOUNCEMENT));
        }

        findPost.removeStudyAnnouncement(announcement);
        studyAnnouncementService.delete(id);
        
        return ResponseEntity.ok().body(new MessageReturnDto().okSuccess(DELETE));
    }
}
