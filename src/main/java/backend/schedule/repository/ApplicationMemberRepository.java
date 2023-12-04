package backend.schedule.repository;

import backend.schedule.entity.ApplicationMember;
import backend.schedule.entity.Member;
import backend.schedule.entity.StudyPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ApplicationMemberRepository extends JpaRepository<ApplicationMember, Long> {
    void deleteByIdAndStudyPost(Long apMemberId, StudyPost studyPost); //반환방식 int로

    @Modifying
    @Query("delete from ApplicationMember ap where ap.id = :applicationMemberId and ap.member.id = :memberId and ap.studyPost.id = :studyPostId")
    int deleteApmember(@Param("applicationMemberId") Long applicationMemberId, @Param("memberId") Long memberId, @Param("studyPostId") Long studyPostId);

    @Query("select ap from ApplicationMember ap join fetch ap.member m where ap.id = :applicationMemberId")
    Optional<ApplicationMember> findApMember(@Param("applicationMemberId") Long applicationMemberId);

    boolean existsByMemberAndStudyPost(Member member, StudyPost studyPost);
    @Modifying
    @Query("delete from ApplicationMember ap where ap.member.id = :memberId")
    void ApplicationMembersWithdrawal(@Param("memberId") Long memberId);
}
