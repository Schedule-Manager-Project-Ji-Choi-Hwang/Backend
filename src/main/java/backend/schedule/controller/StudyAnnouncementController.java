package backend.schedule.controller;

import backend.schedule.dto.MessageReturnDto;
import backend.schedule.dto.Result;
import backend.schedule.dto.ReturnIdDto;
import backend.schedule.dto.studyannouncement.StudyAnnouncementDto;
import backend.schedule.dto.studyannouncement.StudyAnnouncementSetDto;
import backend.schedule.entity.StudyAnnouncement;
import backend.schedule.entity.StudyPost;
import backend.schedule.jwt.JwtTokenExtraction;
import backend.schedule.service.StudyAnnouncementService;
import backend.schedule.service.StudyMemberService;
import backend.schedule.service.StudyPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import static backend.schedule.enumlist.ConfirmAuthor.LEADER;
import static backend.schedule.validation.RequestDataValidation.beanValidation;

@RestController
@RequiredArgsConstructor
public class StudyAnnouncementController {

    private final StudyPostService studyPostService;
    private final StudyMemberService studyMemberService;
    private final JwtTokenExtraction jwtTokenExtraction;
    private final StudyAnnouncementService studyAnnouncementService;

    @Value("${spring.jwt.secretkey}")
    private String mySecretkey;

    /**
     * 스터디 공지 추가
     * Query: 2번
     */
    @PostMapping("/study-board/{studyBoardId}/study-announcements/add")//스터디 공지 추가
    public ResponseEntity<?> studyAnnouncementPost(@Validated @RequestBody StudyAnnouncementDto announcementDto, BindingResult bindingResult,
                                                   @PathVariable Long studyBoardId, HttpServletRequest request) {
        try {
            Long memberId = jwtTokenExtraction.extractionMemberId(request, mySecretkey);
            studyMemberService.studyMemberSearch(memberId, studyBoardId, LEADER);
            StudyPost findPost = studyPostService.findById(studyBoardId);

            if (bindingResult.hasErrors())
                return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(beanValidation(bindingResult)));


            Long announcementId = studyAnnouncementService.save(findPost, announcementDto);

            return ResponseEntity.ok().body(new ReturnIdDto(announcementId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(e.getMessage()));
        }

    }

    /**
     * 스터디 공지 수정 조회
     * Query: 1번
     */
    @GetMapping("/study-board/{studyBoardId}/study-announcements/{announcementId}/edit")
    public ResponseEntity<?> studyAnnouncementUpdateForm(@PathVariable Long studyBoardId, @PathVariable Long announcementId, HttpServletRequest request) {
        try {
            Long memberId = jwtTokenExtraction.extractionMemberId(request, mySecretkey);
            studyMemberService.studyMemberSearch(memberId, studyBoardId, LEADER);
            StudyAnnouncement announcement = studyAnnouncementService.findStudyAnnouncement(announcementId, studyBoardId);

            return ResponseEntity.ok().body(new StudyAnnouncementDto(announcement));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(e.getMessage()));
        }

    }

//    /**
//     * 스터디 공지 조회
//     */
//    @GetMapping("/studyboard/{studyBoardId}/study-announcements/{announcementId}") //공지 단건 조회
//    public ResponseEntity<?> studyAnnouncement(@PathVariable Long studyBoardId, @PathVariable Long announcementId, HttpServletRequest request) {
//        //스터디에 속한 회원이 맞으면 단건조회
//        try {
//            Long memberId = jwtTokenExtraction.extractionMemberId(request, mySecretkey);
//            studyMemberService.studyMemberSearchNoAuthority(memberId, studyBoardId); // 리더,멤버가 다볼 수 있게 하려면 어떻게?
//            StudyAnnouncement announcement = studyAnnouncementService.findStudyAnnouncement(announcementId, studyBoardId);
//
//            return ResponseEntity.ok().body(new StudyAnnouncementDto(announcement));
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(e.getMessage()));
//        }
//    }

    /**
     * 스터디 공지 전체 조회
     * Query: Fetch join이용 1번
     */
    @GetMapping("/study-board/{studyBoardId}/study-announcements")
    public ResponseEntity<?> studyAnnouncementList(@PathVariable Long studyBoardId, HttpServletRequest request) {
        try {
            Long memberId = jwtTokenExtraction.extractionMemberId(request, mySecretkey);
            studyMemberService.studyMemberSearchNoAuthority(memberId, studyBoardId);
            StudyPost studyPost = studyPostService.studyAnnouncements(studyBoardId);

            return ResponseEntity.ok().body(new Result(new StudyAnnouncementSetDto(studyPost)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(e.getMessage()));
        }

    }

    /**
     * 스터디 공지 수정
     * Query: 2번
     */
    @PatchMapping("/study-board/{studyBoardId}/study-announcements/{announcementId}/edit")
    public ResponseEntity<?> studyAnnouncementUpdate(@Validated @RequestBody StudyAnnouncementDto announcementDto, BindingResult bindingResult,
                                                     @PathVariable Long studyBoardId, @PathVariable Long announcementId, HttpServletRequest request) {
        try {
            Long memberId = jwtTokenExtraction.extractionMemberId(request, mySecretkey);
            studyMemberService.studyMemberSearch(memberId, studyBoardId, LEADER);
            StudyAnnouncement announcement = studyAnnouncementService.findStudyAnnouncement(announcementId, studyBoardId);

            if (bindingResult.hasErrors())
                return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(beanValidation(bindingResult)));

            studyAnnouncementService.updateStudyAnnouncement(announcement, announcementDto);

            return ResponseEntity.ok().body(new ReturnIdDto(studyBoardId, announcementId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(e.getMessage()));
        }

    }

    /**
     * 스터디 공지 삭제
     * Query: 5번
     */
    @Transactional
    @DeleteMapping("/study-board/{studyBoardId}/study-announcements/{announcementId}/delete")
    public ResponseEntity<?> studyAnnouncementDelete(@PathVariable Long studyBoardId, @PathVariable Long announcementId, HttpServletRequest request) {
        try {
            Long memberId = jwtTokenExtraction.extractionMemberId(request, mySecretkey);
            studyMemberService.studyMemberSearch(memberId, studyBoardId, LEADER);

            StudyAnnouncement announcement = studyAnnouncementService.findStudyAnnouncement(announcementId, studyBoardId);

            studyAnnouncementService.removeAnnouncement(announcement);

            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(e.getMessage()));
        }

    }
}
