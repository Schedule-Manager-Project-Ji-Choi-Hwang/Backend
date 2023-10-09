package backend.schedule.repository;

import backend.schedule.entity.StudyComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyCommentRepository extends JpaRepository<StudyComment, Long> {
}
