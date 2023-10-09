package backend.schedule.repository;

import backend.schedule.entity.PersonalSubject;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonalSubjectRepository extends JpaRepository<PersonalSubject, Long> {
}
