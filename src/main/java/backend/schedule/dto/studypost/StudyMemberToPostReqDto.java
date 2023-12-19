package backend.schedule.dto.studypost;

import backend.schedule.entity.StudyMember;
import backend.schedule.enumlist.ConfirmAuthor;
import backend.schedule.enumlist.FieldTag;
import lombok.Data;

@Data
public class StudyMemberToPostReqDto {

    private Long id;

    private String studyName;

    private ConfirmAuthor authority;

    private FieldTag tag;

    private int currentMember;

    private int recruitMember;

    public StudyMemberToPostReqDto(StudyMember studyMember) {
        this.id = studyMember.getStudyPost().getId();
        this.studyName = studyMember.getStudyPost().getStudyName();
        this.authority = studyMember.getConfirmAuthor();
        this.tag = studyMember.getStudyPost().getTag();
        this.currentMember = studyMember.getStudyPost().getStudyMembers().size();
        this.recruitMember = studyMember.getStudyPost().getRecruitMember();
    }
}
