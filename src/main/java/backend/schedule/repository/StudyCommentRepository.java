package backend.schedule.repository;

import backend.schedule.entity.StudyComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface StudyCommentRepository extends JpaRepository<StudyComment, Long> {

    Optional<StudyComment> findByStudyAnnouncementIdAndIdAndMemberId(Long studyAnnouncementId, Long studyCommentId, Long memberId); //이름 너무 김 나중에 쿼리 직접작성

    @Modifying
    @Query("delete from StudyComment sc where sc.studyAnnouncement.id = :studyAnnouncementId and sc.id = :studyCommentId and sc.member.id = :memberId")
    void commentRemove(@Param("studyAnnouncementId") Long studyAnnouncementId, @Param("studyCommentId") Long studyCommentId, @Param("memberId") Long memberId);
}
