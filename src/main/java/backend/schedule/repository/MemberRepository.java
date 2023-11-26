package backend.schedule.repository;

import backend.schedule.entity.Member;
import backend.schedule.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByLoginId(String loginId);

    boolean existsByNickname(String nickname);

//    boolean existsByLoginIdAndEmail(String loginId, String email);

    Optional<Member> findByLoginIdAndEmail(String loginId, String email);

    Optional<Member> findByLoginId(String loginId);

    Optional<Member> findByEmail(String email);

//    @Query("SELECT m FROM Member m " +
//            "LEFT JOIN FETCH m.personalSubjects ps " +
//            "WHERE m.id = :id")
//    Optional<Member> findByIdWithPersonalSubjects(@Param("id") Long id);

    @Query("SELECT DISTINCT ps FROM Subject ps " +
            "LEFT JOIN FETCH ps.schedules sc " +
            "WHERE ps.member.id = :memberId and sc.period = :date")
    List<Subject> findPersonalSubjectsWithSchedulesByMemberId(@Param("memberId") Long memberId, @Param("date")LocalDate date);
}
