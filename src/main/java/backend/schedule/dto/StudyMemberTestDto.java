package backend.schedule.dto;

import backend.schedule.entity.StudyMember;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.aspectj.weaver.ast.Test;

@Getter
@NoArgsConstructor
@Setter
public class StudyMemberTestDto {

    private Long studyPostId;

    public StudyMemberTestDto(StudyMember studyMember) {
        this.studyPostId = studyMember.getStudyPost().getId();
    }
}
