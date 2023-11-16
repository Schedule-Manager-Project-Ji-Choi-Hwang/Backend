package backend.schedule.repository;

import backend.schedule.entity.ApplicationMember;
import backend.schedule.entity.Member;
import backend.schedule.entity.StudyPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ApplicationMemberRepository extends JpaRepository<ApplicationMember, Long> {
    void deleteByIdAndStudyPost(Long apMemberId, StudyPost studyPost);

    boolean existsByMemberAndStudyPost(Member member, StudyPost studyPost);
}
