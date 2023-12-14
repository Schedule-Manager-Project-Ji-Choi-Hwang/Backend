package backend.schedule.dto.studypost;

import backend.schedule.dto.studyannouncement.StudyGroupAnnouncementsResDto;
import backend.schedule.dto.studymember.StudyGroupMembersResDto;
import backend.schedule.entity.StudyPost;
import backend.schedule.enumlist.ConfirmAuthor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class AnnouncementsAndStudyMembersDto {

    private Long studyPostId;

    private String studyName;

    private boolean myAuthority;

    private List<StudyGroupAnnouncementsResDto> announcementList;

    private List<StudyGroupMembersResDto> studyMemberList;

    public AnnouncementsAndStudyMembersDto(StudyPost studyPostSa, StudyPost studyPostSm, boolean myAuthority) {
        this.studyPostId = studyPostSa.getId();
        this.studyName = studyPostSa.getStudyName();
        this.myAuthority = myAuthority;
        this.announcementList = studyPostSa.getStudyAnnouncements().stream()
                .map(StudyGroupAnnouncementsResDto::new)
                .collect(Collectors.toList());
        this.studyMemberList = studyPostSm.getStudyMembers().stream()
                .map(StudyGroupMembersResDto::new)
                .collect(Collectors.toList());
    }
}
