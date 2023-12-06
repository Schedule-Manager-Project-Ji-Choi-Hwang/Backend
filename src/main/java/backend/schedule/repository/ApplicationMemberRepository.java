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
    @Modifying
    @Query("delete from ApplicationMember a where a.id = :apMemberId and a.studyPost.id = :studyBoardId")
    void deleteByIdAndStudyPost(@Param("apMemberId") Long apMemberId, @Param("studyBoardId") Long studyBoardId); //반환방식 int로

    @Modifying
    @Query("delete from ApplicationMember ap where ap.id = :applicationMemberId and ap.studyPost.id = :studyPostId")
    int deleteApmember(@Param("applicationMemberId") Long applicationMemberId, @Param("studyPostId") Long studyPostId);

    @Query("select ap from ApplicationMember ap join fetch ap.member m where ap.id = :applicationMemberId and ap.studyPost.id = :studyBoardId")
    Optional<ApplicationMember> findApMember(@Param("applicationMemberId") Long applicationMemberId, @Param("studyBoardId") Long studyBoardId);

    //boolean existsByMemberAndStudyPost(Member member, StudyPost studyPost);
    @Query("SELECT CASE WHEN COUNT(a) = 1 THEN true ELSE false END FROM ApplicationMember a WHERE a.member.id = :memberId AND a.studyPost.id = :studyBoardId")
    boolean existsApplicationMember(@Param("memberId") Long memberId, @Param("studyBoardId") Long studyBoardId);


    @Modifying
    @Query("delete from ApplicationMember ap where ap.member.id = :memberId")
    void ApplicationMembersWithdrawal(@Param("memberId") Long memberId);
}
