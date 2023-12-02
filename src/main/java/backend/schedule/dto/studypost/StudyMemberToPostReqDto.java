package backend.schedule.dto.studypost;

import backend.schedule.entity.StudyMember;
import backend.schedule.enumlist.ConfirmAuthor;
import lombok.Data;

@Data
public class StudyMemberToPostReqDto {

    private Long studyMemberId;

    private ConfirmAuthor authority;

//    private List<MyStudyPostListReqDto> studyPostList;

    private String studyName;

    private String post;

    private int recruitMember;

    public StudyMemberToPostReqDto(StudyMember studyMember) {
        this.studyMemberId = studyMember.getId();
        this.authority = studyMember.getConfirmAuthor();
        this.studyName = studyMember.getStudyPost().getStudyName();
        this.post = studyMember.getStudyPost().getPost();
        this.recruitMember = studyMember.getStudyPost().getRecruitMember();
    }
}
