package backend.schedule.repository;

import backend.schedule.entity.Member;
import backend.schedule.entity.PersonalSubject;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    @Query("SELECT DISTINCT ps FROM PersonalSubject ps " +
            "LEFT JOIN FETCH ps.schedules " +
            "WHERE ps.member.id = :memberId")
    List<PersonalSubject> findPersonalSubjectsWithSchedulesByMemberId(@Param("memberId") Long memberId);
}
