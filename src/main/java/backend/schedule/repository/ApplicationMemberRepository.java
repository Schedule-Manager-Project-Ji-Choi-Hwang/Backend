package backend.schedule.repository;

import backend.schedule.entity.ApplicationMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationMemberRepository extends JpaRepository<ApplicationMember, Long> {
}
