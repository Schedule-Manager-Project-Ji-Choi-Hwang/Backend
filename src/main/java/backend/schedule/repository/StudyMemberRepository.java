package backend.schedule.repository;

import backend.schedule.entity.Member;
import backend.schedule.entity.StudyMember;
import backend.schedule.entity.StudyPost;
import backend.schedule.entity.StudySchedule;
import backend.schedule.enumlist.ConfirmAuthor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface StudyMemberRepository extends JpaRepository<StudyMember, Long> {

    @Query("select sm from StudyMember sm where sm.member.id = :memberId and sm.studyPost.id = :studyBoardId and sm.confirmAuthor = :confirmAuthor")
    Optional<StudyMember> studyMemberSearch(@Param("memberId") Long memberId, @Param("studyBoardId") Long studyBoardId, @Param("confirmAuthor") ConfirmAuthor confirmAuthor);

    @Query("select sm from StudyMember sm where sm.member.id = :memberId and sm.studyPost.id = :studyBoardId")
    Optional<StudyMember> studyMemberSearchNoAuthority(@Param("memberId") Long memberId, @Param("studyBoardId") Long studyBoardId);

    @Query("select sm from StudyMember sm join fetch sm.studyPost sp join fetch sp.studyMembers where sm.member.id = :memberId and sm.studyPost.id = :studyBoardId")
    Optional<StudyMember> studyMemberGetStudyPost(@Param("memberId") Long memberId, @Param("studyBoardId") Long studyBoardId);

    @Query("select sp from StudyPost sp join fetch sp.studyMembers sm join fetch sm.member where sp.id = :studyBoardId")
    Optional<StudyPost> studyMembersByStudyboardId(@Param("studyBoardId") Long studyBoardId);

    @Query("select distinct sm from StudyMember sm join fetch  sm.studyPost where sm.member.id = :memberId")
    List<StudyMember> MainPageStudyMembers(@Param("memberId") Long memberId);

    @Query("select sm from StudyMember sm join fetch  sm.studyPost where sm.member.id = :memberId and sm.confirmAuthor = :confirmAuthor")
    List<StudyMember> myPostList(@Param("memberId") Long memberId, @Param("confirmAuthor") ConfirmAuthor confirmAuthor);

    boolean existsByMemberAndStudyPost(Member member, StudyPost studyPost);

    @Query("select sm from StudyMember sm join fetch sm.studyPost sp join fetch sp.studyMembers where sm.member.id = :memberId")
    List<StudyMember> findStudyMembersWithdrawal(@Param("memberId") Long memberId);

    @Modifying
    @Query("delete from StudyMember sm where sm.studyPost.id = :studyBoardId and sm.id = :studyMemberId and sm.confirmAuthor = :confirmAuthor")
    int deleteStudyMember(@Param("studyBoardId") Long studyBoardId, @Param("studyMemberId") Long studyMemberId, @Param("confirmAuthor") ConfirmAuthor confirmAuthor);

    @Query("select distinct sm from StudyMember sm join fetch sm.studyPost sp join fetch sp.studySchedules ss where sm.member.id = :memberId and ss.period = :date")
    List<StudyMember> findStudymembers(@Param("memberId") Long MemberId, @Param("date")LocalDate date);
}
