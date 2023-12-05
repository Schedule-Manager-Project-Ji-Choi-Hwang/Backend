package backend.schedule.repository;

import backend.schedule.entity.Member;
import backend.schedule.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
    List<Subject> findByMember(Member member);

    @Query("select distinct ps from Subject ps join fetch ps.schedules sc where ps.member.id = :memberId and sc.period = :date")
    List<Subject> findSubjectsWithSchedulesByMemberId(@Param("memberId") Long memberId, @Param("date") LocalDate date);

}
