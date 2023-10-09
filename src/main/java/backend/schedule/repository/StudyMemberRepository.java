package backend.schedule.repository;

import backend.schedule.entity.StudyMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyMemberRepository extends JpaRepository<StudyMember, Long> {
}
