package backend.schedule.repository;

import backend.schedule.entity.Member;
import backend.schedule.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
    List<Subject> findByMember(Member member);

    @Query("select p from Subject p where p.subjectName = :subjectName")
    Optional<Subject> findBySubjectName(@Param("subjectName") String subjectName);
}
