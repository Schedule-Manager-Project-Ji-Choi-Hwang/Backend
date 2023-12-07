package backend.schedule.dto.studypost;

import backend.schedule.dto.studyannouncement.StudyGroupAnnouncementsResDto;
import backend.schedule.dto.studymember.StudyGroupMembersResDto;
import backend.schedule.entity.StudyPost;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class AnnouncementsAndStudyMembersDto {

    private Long studyPostId;

    private String studyName;

    private List<StudyGroupAnnouncementsResDto> announcementList;

    private List<StudyGroupMembersResDto> studyMemberList;

    public AnnouncementsAndStudyMembersDto(StudyPost studyPost) {
        this.studyPostId = studyPost.getId();
        this.studyName = studyPost.getStudyName();
        this.announcementList = studyPost.getStudyAnnouncements().stream()
                .map(StudyGroupAnnouncementsResDto::new)
                .collect(Collectors.toList());
        this.studyMemberList = studyPost.getStudyMembers().stream()
                .map(StudyGroupMembersResDto::new)
                .collect(Collectors.toList());
    }
}
