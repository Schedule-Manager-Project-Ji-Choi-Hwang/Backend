package backend.schedule.repository;

import backend.schedule.entity.Member;
import backend.schedule.entity.PersonalSubject;
import backend.schedule.entity.StudyMember;
import backend.schedule.entity.StudyPost;
import backend.schedule.enumlist.ConfirmAuthor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StudyMemberRepository extends JpaRepository<StudyMember, Long> {

    @Query("select sm from StudyMember sm where sm.member.id = :memberId and sm.studyPost.id = :studyBoardId and sm.confirmAuthor = :confirmAuthor")
    Optional<StudyMember> findByMemberAndStudyPost(@Param("memberId") Long memberId,
                                                   @Param("studyBoardId") Long studyBoardId,
                                                   @Param("confirmAuthor") ConfirmAuthor confirmAuthor);

    boolean existsByMemberAndStudyPost(Member member, StudyPost studyPost);

    @Query("SELECT sp FROM StudyPost sp " +
            "LEFT JOIN FETCH sp.studyMembers " +
            "WHERE sp.id = :studyBoardId")
    Optional<StudyPost> studyMembersByStudyboardId(@Param("studyBoardId") Long studyBoardId);

    @Query("SELECT DISTINCT sm FROM StudyMember sm " +
            "LEFT JOIN FETCH  sm.studyPost " +
            "WHERE sm.member.id = :memberId")
    List<StudyMember> MainPageStudyMembers(@Param("memberId") Long memberId);

}
