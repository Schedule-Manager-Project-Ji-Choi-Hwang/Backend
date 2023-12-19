package backend.schedule;


import backend.schedule.dto.studyannouncement.StudyAnnouncementDto;
import backend.schedule.entity.*;
import backend.schedule.enumlist.ConfirmAuthor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;

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
//            for (int i = 0; i < 10000; i++) {
//                em.persist(new StudyPost("" + i, LocalDate.now()));
//            }
            // 민현 추가
            String[] subjectList = {"Java", "Python", "C++"};
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            Member testMember = new Member("hji1235", encoder.encode("456456"), "CMH", "hji1235@naver.com");
            em.persist(testMember);

            Member testMember2 = new Member("hji1234", encoder.encode("456456"), "CM", "hji1234@naver.com");
            em.persist(testMember2);
            Member testMember3 = new Member("hji1233", encoder.encode("456456"), "C", "hji1233@naver.com");
            em.persist(testMember3);
            for (String subject : subjectList) {
                Subject testSubject = new Subject(testMember, subject);
                testMember.addPersonalSubject(testSubject);
                em.persist(testSubject);

                for (int i = 1; i < 4; i++) {
                    Schedule testSchedule = new Schedule(subject + " 공부" + i, LocalDate.now(), testSubject);
                    testSubject.addSchedules(testSchedule);
                    em.persist(testSchedule);
                }
            }
            StudyPost testStudyPost1 = new StudyPost("프로젝트!!");
            em.persist(testStudyPost1);
            // 스터디 멤버 추가 부분
            StudyMember testStudyMember = new StudyMember(testMember, ConfirmAuthor.LEADER);
            testStudyPost1.addStudyMember(testStudyMember);
            em.persist(testStudyMember);

            StudyMember testStudyMember4 = new StudyMember(testMember2, ConfirmAuthor.MEMBER);
            testStudyPost1.addStudyMember(testStudyMember4);
            em.persist(testStudyMember4);

            StudyMember testStudyMember5 = new StudyMember(testMember3, ConfirmAuthor.MEMBER);
            testStudyPost1.addStudyMember(testStudyMember5);
            em.persist(testStudyMember5);

            //공지
            for (int i=0; i<5; i++) {
                StudyAnnouncement testStudyAnnouncement = new StudyAnnouncement("테스트 공지" + i, "테스트 내용" + i);
                testStudyPost1.addStudyAnnouncements(testStudyAnnouncement);
                StudyComment testStudyComment = new StudyComment("대대댓글" + i);
                testStudyAnnouncement.addStudyComment(testStudyComment);
                testMember.addStudyComments(testStudyComment);
                em.persist(testStudyAnnouncement);
                em.persist(testStudyComment);
            }


            for (int i = 1; i < 7; i++) {
                StudySchedule testSchedule = new StudySchedule().testSchedule(testStudyPost1.getStudyName() + i, LocalDate.now());
                testStudyPost1.addStudySchedule(testSchedule);
                em.persist(testSchedule);
            }

            StudyPost testStudyPost2 = new StudyPost("프로젝트2!!");
            em.persist(testStudyPost2);
            StudyMember testStudyMember2 = new StudyMember(testMember, ConfirmAuthor.LEADER);
            testStudyPost2.addStudyMember(testStudyMember2);
            em.persist(testStudyMember2);

            // 신청 회원 추가
            ApplicationMember testApplicationMember1 = new ApplicationMember(testMember2);
            testStudyPost2.addApplicationMember(testApplicationMember1);
            em.persist(testApplicationMember1);

            ApplicationMember testApplicationMember2 = new ApplicationMember(testMember3);
            testStudyPost2.addApplicationMember(testApplicationMember2);
            em.persist(testApplicationMember2);

            for (int i = 1; i < 7; i++) {
                StudySchedule testSchedule = new StudySchedule().testSchedule(testStudyPost2.getStudyName() + i, LocalDate.now());
                testStudyPost2.addStudySchedule(testSchedule);
                em.persist(testSchedule);
            }

            StudyPost testStudyPost3 = new StudyPost("프로젝트3!!");
            em.persist(testStudyPost3);
            StudyMember testStudyMember3 = new StudyMember(testMember, ConfirmAuthor.LEADER);
            testStudyPost3.addStudyMember(testStudyMember3);
            em.persist(testStudyMember3);

            ApplicationMember testApplicationMember3 = new ApplicationMember(testMember2);
            testStudyPost3.addApplicationMember(testApplicationMember3);
            em.persist(testApplicationMember3);


            for (int i = 1; i < 7; i++) {
                StudySchedule testSchedule = new StudySchedule().testSchedule(testStudyPost3.getStudyName() + i, LocalDate.now());
                testStudyPost3.addStudySchedule(testSchedule);
                em.persist(testSchedule);
            }

            for (int i = 0; i < 1000; i++) {
                em.persist(new StudyPost("" + i));
            }
        }
    }
}