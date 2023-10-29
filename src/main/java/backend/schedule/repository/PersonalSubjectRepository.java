package backend.schedule.repository;

import backend.schedule.dto.PersonalSubjectResDto;
import backend.schedule.entity.Member;
import backend.schedule.entity.PersonalSubject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PersonalSubjectRepository extends JpaRepository<PersonalSubject, Long> {
    List<PersonalSubject> findByMember(Member member);
}
