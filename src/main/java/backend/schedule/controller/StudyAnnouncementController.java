package backend.schedule.controller;

import backend.schedule.dto.MessageReturnDto;
import backend.schedule.dto.Result;
import backend.schedule.dto.ReturnIdDto;
import backend.schedule.dto.studyannouncement.StudyAnnouncementDto;
import backend.schedule.dto.studyannouncement.StudyAnnouncementSetDto;
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
     * 스터디 공지 추가
     * Query: 2번
     */
    @Transactional
    @PostMapping("/studyboard/{studyBoardId}/study-announcements/add")//스터디 공지 추가
    public ResponseEntity<?> studyAnnouncementPost(@Validated @RequestBody StudyAnnouncementDto announcementDto,
                                                   BindingResult bindingResult, @PathVariable Long studyBoardId) {
        StudyPost findPost = studyPostService.findById(studyBoardId);

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
    @GetMapping("/studyboard/{studyBoardId}/study-announcements/{announcementId}/edit")
    public ResponseEntity<?> studyAnnouncementUpdateForm(@PathVariable Long announcementId) {
        StudyAnnouncement announcement = studyAnnouncementService.findById(announcementId);

        if (announcement == null) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(ANNOUNCEMENT));
        }

        return ResponseEntity.ok().body(new StudyAnnouncementDto(announcement));
    }

    /**
     * 스터디 공지 조회
     * Query: Fetch join이용 1번
     */
    @GetMapping("/studyboard/{studyBoardId}/study-announcements/{announcementId}") //공지 단건 조회
    public Result studyAnnouncement(@PathVariable Long studyBoardId, @PathVariable Long announcementId) {
        StudyPost studyPost = studyPostService.studyAnnouncement(studyBoardId, announcementId);

        return new Result(new StudyAnnouncementSetDto(studyPost));
    }

    /**
     * 스터디 공지 전체 조회
     * Query: Fetch join이용 1번
     */
    @GetMapping("/studyboard/{studyBoardId}/study-announcements") //전체 공지 조회
    public Result studyAnnouncementList(@PathVariable Long studyBoardId) {
        StudyPost studyPost = studyPostService.studyAnnouncements(studyBoardId);

        return new Result(new StudyAnnouncementSetDto(studyPost));
    }

    /**
     * 스터디 공지 수정
     * Query: 2번
     */
    @Transactional
    @PatchMapping("/studyboard/{studyBoardId}/study-announcements/{announcementId}/edit")
    public ResponseEntity<?> studyAnnouncementUpdate(
            @Validated @RequestBody StudyAnnouncementDto announcementDto,
            BindingResult bindingResult, @PathVariable Long studyBoardId, @PathVariable Long announcementId) {

        StudyAnnouncement announcement = studyAnnouncementService.findById(announcementId);

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

        return ResponseEntity.ok().body(new ReturnIdDto(studyBoardId, announcementId));
    }

    /**
     * 스터디 공지 삭제
     * Query: 5번
     */
    @Transactional
    @DeleteMapping("/studyboard/{studyBoardId}/study-announcements/{announcementId}/delete")
    public ResponseEntity<?> studyAnnouncementDelete(@PathVariable Long studyBoardId, @PathVariable Long announcementId) {
        StudyPost findPost = studyPostService.findById(studyBoardId);
        StudyAnnouncement announcement = studyAnnouncementService.findById(announcementId);

        if (findPost == null) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(POST));
        } else if (announcement == null) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(ANNOUNCEMENT));
        }

        findPost.removeStudyAnnouncement(announcement);
        studyAnnouncementService.delete(announcementId);

        return ResponseEntity.ok().body(new MessageReturnDto().okSuccess(DELETE));
    }
}
