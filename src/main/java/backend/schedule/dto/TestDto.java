package backend.schedule.dto;

import backend.schedule.entity.Member;
import backend.schedule.entity.StudyMember;
import backend.schedule.entity.StudyPost;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@Setter
public class TestDto {

    private Long memberId;

    private List<StudyMemberTestDto> studyMembers;

    public TestDto(Member member) {
        this.memberId = member.getId();
        this.studyMembers = member.getStudyMembers().stream()
                .map(StudyMemberTestDto::new)
                .collect(Collectors.toList());
    }
}
