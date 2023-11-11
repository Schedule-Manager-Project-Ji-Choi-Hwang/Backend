package backend.schedule.repository;

import backend.schedule.dto.PersonalSubjectResDto;
import backend.schedule.entity.Member;
import backend.schedule.entity.PersonalSubject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PersonalSubjectRepository extends JpaRepository<PersonalSubject, Long> {
    List<PersonalSubject> findByMember(Member member);

    @Query("select p from PersonalSubject p where p.subjectName = :subjectName")
    Optional<PersonalSubject> findBySubjectName(@Param("subjectName") String subjectName);
}
