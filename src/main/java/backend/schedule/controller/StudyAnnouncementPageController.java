package backend.schedule.controller;

import backend.schedule.dto.MessageReturnDto;
import backend.schedule.dto.Result;
import backend.schedule.dto.studyannouncement.AnnouncementAndCommentsDto;
import backend.schedule.dto.studyannouncement.StudyAnnouncementDto;
import backend.schedule.dto.studypost.AnnouncementsAndStudyMembersDto;
import backend.schedule.entity.StudyAnnouncement;
import backend.schedule.entity.StudyPost;
import backend.schedule.jwt.JwtTokenExtraction;
import backend.schedule.service.StudyAnnouncementService;
import backend.schedule.service.StudyMemberService;
import backend.schedule.service.StudyPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
public class StudyAnnouncementPageController {
    private final JwtTokenExtraction jwtTokenExtraction;
    private final StudyMemberService studyMemberService;
    private final StudyPostService studyPostService;
    private final StudyAnnouncementService studyAnnouncementService;

    @Value("${spring.jwt.secretkey}")
    private String mySecretkey;

    @GetMapping("/study-board/{studyBoardId}/study-announcements/{announcementId}")
    public ResponseEntity<?> AnnouncementDetailPage(@PathVariable Long studyBoardId, @PathVariable Long announcementId, HttpServletRequest request) {
        try {
            Long memberId = jwtTokenExtraction.extractionMemberId(request, mySecretkey);
            studyMemberService.studyMemberSearchNoAuthority(memberId, studyBoardId);
            StudyAnnouncement announcement = studyAnnouncementService.announcementCommentList(announcementId, studyBoardId);
            AnnouncementAndCommentsDto returnData = studyAnnouncementService.returnAnnouncementAndComments(announcement);


            return ResponseEntity.ok().body(new Result(returnData));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(e.getMessage()));
        }
    }

    @GetMapping("/study-board/{studyBoardId}/detail")
    public ResponseEntity<?> studyGroupDetailPage(@PathVariable Long studyBoardId, HttpServletRequest request) {
        try {
            Long memberId = jwtTokenExtraction.extractionMemberId(request, mySecretkey);
            studyMemberService.studyMemberSearchNoAuthority(memberId, studyBoardId);
            StudyPost findStudyPost = studyPostService.findStudyGroupDetailPage(studyBoardId);
            AnnouncementsAndStudyMembersDto returnData = studyPostService.returnToStudyGroupInfo(findStudyPost);

            return ResponseEntity.ok().body(new Result(returnData));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageReturnDto().badRequestFail(e.getMessage()));
        }


    }
}
