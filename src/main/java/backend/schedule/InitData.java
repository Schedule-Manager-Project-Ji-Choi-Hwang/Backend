package backend.schedule;


import backend.schedule.entity.Member;
import backend.schedule.entity.Schedule;
import backend.schedule.entity.StudyPost;
import backend.schedule.entity.Subject;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;
import java.util.List;

@Profile("local")
@Component
@RequiredArgsConstructor
public class InitData {
    private final InitDataService initDataService;

    @PostConstruct
    public void init() {
        initDataService.init();
    }


    @Component
    static class InitDataService {

        @PersistenceContext
        EntityManager em;

        @Transactional
        public void init() {
            for (int i = 0; i < 10000; i++) {
                em.persist(new StudyPost("" + i, LocalDate.now()));
            }
            // 민현 추가
            String[] subjectList = {"Java", "Python", "C++"};
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            Member testMember = new Member("hji1235", encoder.encode("456456"), "CMH", "hji1235@naver.com");
            em.persist(testMember);
            for (String subject : subjectList) {
                Subject testSubject = new Subject(testMember, subject);
                testMember.addPersonalSubject(testSubject);
                em.persist(testSubject);

                for (int i=1; i<4; i++) {
                    Schedule testSchedule = new Schedule(subject + " 공부" + i, LocalDate.now().plusDays(i), testSubject);
                    testSubject.addSchedules(testSchedule);
                    em.persist(testSchedule);
                }
            }
            // 민현 추가
        }
    }
}