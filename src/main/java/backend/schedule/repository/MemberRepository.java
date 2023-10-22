package backend.schedule.repository;

import backend.schedule.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByLoginId(String loginId);

    boolean existsByNickname(String nickname);

    boolean existsByLoginIdAndEmail(String loginId, String email);

    Optional<Member> findByLoginId(String loginId);

    Optional<Member> findByEmail(String email);
}
