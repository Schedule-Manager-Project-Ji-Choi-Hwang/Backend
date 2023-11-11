package backend.schedule.repository;

import backend.schedule.entity.Member;
import backend.schedule.entity.StudyMember;
import backend.schedule.enumlist.ConfirmAuthor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface StudyMemberRepository extends JpaRepository<StudyMember, Long> {

    @Query("select s from StudyMember s where s.member.id = :memberId and s.studyPost.id = :studyPostId and" +
            " s.confirmAuthor = :confirmAuthor")
    Optional<StudyMember> findByMemberAndStudyPost(@Param("memberId") Long memberId,
                                                   @Param("studyPostId") Long studyPostId,
                                                   @Param("confirmAuthor") ConfirmAuthor confirmAuthor);
}
