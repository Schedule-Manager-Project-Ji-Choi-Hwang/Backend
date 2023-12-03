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

    Optional<Member> findByLoginIdAndEmail(String loginId, String email);

    Optional<Member> findByLoginId(String loginId);

    Optional<Member> findByEmail(String email);
}
