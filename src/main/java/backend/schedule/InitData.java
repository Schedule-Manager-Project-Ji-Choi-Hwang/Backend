package backend.schedule;


import backend.schedule.entity.*;
import backend.schedule.enumlist.ConfirmAuthor;
import backend.schedule.enumlist.FieldTag;
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

            // 개인 과목 리스트
            String[] subjectList = {"Java", "Python", "C++"};

            // 색상 리스트
            String[] colorList = {"black", "white", "blue"};

            // 비밀번호 암호화 encoder
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

            // 멤버 1 등록
            Member testMember = new Member("hji1235", encoder.encode("456456"), "CMH", "hji1235@naver.com");
            em.persist(testMember);

            // 멤버 2 등록
            Member testMember2 = new Member("hji1234", encoder.encode("456456"), "CM", "hji1234@naver.com");
            em.persist(testMember2);

            // 멤버 3 등록
            Member testMember3 = new Member("hji1233", encoder.encode("456456"), "C", "hji1233@naver.com");
            em.persist(testMember3);

            // 멤버 4 등록 (대량 스터디 게시글 작성자)
            Member testMember4 = new Member("hji1236", encoder.encode("456456"), "전재학", "hji1236@naver.com");
            em.persist(testMember4);

            // 멤버 4의 대량 스터디 게시글 등록
            for (int i = 0; i < 100; i++) {
                StudyPost initStudyPost = new StudyPost("테스트공부(스터디)" + i, FieldTag.DEVELOP, 20, false, "부산", "테스트용 스터디 입니다. 가입 탈퇴 자유!!");
                em.persist(initStudyPost);
                StudyMember initStudyMember = new StudyMember(testMember4, ConfirmAuthor.LEADER);
                initStudyPost.addStudyMember(initStudyMember);
                em.persist(initStudyMember);
            }

            // 개인 과목 리스트 순회 및 등록
            for (int i = 0; i < 3; i++) {
                Subject testSubject = new Subject(testMember, subjectList[i], colorList[i]);
                testMember.addPersonalSubject(testSubject);
                em.persist(testSubject);

                // 개인 과목 순회 및 일정 등록 (오늘 날짜)
                for (int j = 1; j < 4; j++) {
                    Schedule testSchedule = new Schedule(j + "장 공부하기", LocalDate.now(), testSubject);
                    testSubject.addSchedules(testSchedule);
                    em.persist(testSchedule);
                }

                // 개인 과목 순회 및 일정 등록 (내일 날짜)
                for (int j = 1; j < 4; j++) {
                    Schedule testSchedule = new Schedule((j + 4) + "장 공부하기", LocalDate.now().plusDays(1), testSubject);
                    testSubject.addSchedules(testSchedule);
                    em.persist(testSchedule);
                }

                // 개인 과목 순회 및 일정 등록 (다음 주 날짜)
                for (int j = 1; j < 4; j++) {
                    Schedule testSchedule = new Schedule((j + 8) + "장 공부하기", LocalDate.now().plusWeeks(1), testSubject);
                    testSubject.addSchedules(testSchedule);
                    em.persist(testSchedule);
                }
            }
            // hji1235 멤버의 스터디 게시글1 등록
            StudyPost testStudyPost1 = new StudyPost("정보처리기사(스터디)", FieldTag.DEVELOP, 20, false, "부산", "부산 정처기 스터디입니다. 부산사람 환영");
            em.persist(testStudyPost1);
            StudyMember testStudyMember = new StudyMember(testMember, ConfirmAuthor.LEADER);
            testStudyPost1.addStudyMember(testStudyMember);
            em.persist(testStudyMember);

            // 스터디 게시글1에 hji1234 멤버 가입
            StudyMember testStudyMember4 = new StudyMember(testMember2, ConfirmAuthor.MEMBER);
            testStudyPost1.addStudyMember(testStudyMember4);
            em.persist(testStudyMember4);

            // 스터디 게시글1에 hji1233 멤버 가입
            StudyMember testStudyMember5 = new StudyMember(testMember3, ConfirmAuthor.MEMBER);
            testStudyPost1.addStudyMember(testStudyMember5);
            em.persist(testStudyMember5);

            // 스터디 게시글1에 대한 공지 및 댓글 등록
            for (int i = 0; i < 5; i++) {
                StudyAnnouncement testStudyAnnouncement = new StudyAnnouncement("온라인 줌 회의 링크" + i, "http://naver.com" + i);
                testStudyPost1.addStudyAnnouncements(testStudyAnnouncement);
                StudyComment testStudyComment = new StudyComment("네 알겠습니다. 확인했습니다." + i);
                testStudyAnnouncement.addStudyComment(testStudyComment);
                testMember.addStudyComments(testStudyComment);
                em.persist(testStudyAnnouncement);
                em.persist(testStudyComment);
            }

            // 스터디 게시글1에 대한 일정 등록
            for (int i = 0; i < 4; i++) {
                StudySchedule testSchedule = new StudySchedule().testSchedule("정처기 정기 회의" + i, LocalDate.now().plusDays(i));
                testStudyPost1.addStudySchedule(testSchedule);
                em.persist(testSchedule);
            }

            // 스터디 게시글1에 대한 신청멤버 대량 등록
            for (int i = 0; i < 20; i++) {
                Member member = new Member("" + i, encoder.encode("456456"), "CMH" + i, "hji1235@naver.com" + i);
                ApplicationMember applicationMember = new ApplicationMember(member);
                em.persist(member);
                em.persist(applicationMember);
                testStudyPost1.addApplicationMember(applicationMember);
            }

            // hji1235 멤버의 스터디 게시글2 등록
            StudyPost testStudyPost2 = new StudyPost("AWS 자격증(스터디)", FieldTag.ETC, 15, true, "온라인", "AWS 자격증 스터디입니다. 경험자 환영");
            em.persist(testStudyPost2);
            StudyMember testStudyMember2 = new StudyMember(testMember, ConfirmAuthor.LEADER);
            testStudyPost2.addStudyMember(testStudyMember2);
            em.persist(testStudyMember2);

            // 스터디 게시글2에 대한 신청 회원(hji1234) 등록
            ApplicationMember testApplicationMember1 = new ApplicationMember(testMember2);
            testStudyPost2.addApplicationMember(testApplicationMember1);
            em.persist(testApplicationMember1);

            // 스터디 게시글2에 대한 신청 회원(hji1233) 등록
            ApplicationMember testApplicationMember2 = new ApplicationMember(testMember3);
            testStudyPost2.addApplicationMember(testApplicationMember2);
            em.persist(testApplicationMember2);

            // 스터디 게시글2에 대한 일정 등록
            for (int i = 1; i < 4; i++) {
                StudySchedule testSchedule = new StudySchedule().testSchedule("AWS 정보 공유 회의" + i, LocalDate.now());
                testStudyPost2.addStudySchedule(testSchedule);
                em.persist(testSchedule);
            }

            StudyPost testStudyPost3 = new StudyPost("코딩 테스트(스터디)", FieldTag.TOEIC, 5, false, "서울", "백준 실버3 이상 구해요.");
            em.persist(testStudyPost3);
            StudyMember testStudyMember3 = new StudyMember(testMember, ConfirmAuthor.LEADER);
            testStudyPost3.addStudyMember(testStudyMember3);
            em.persist(testStudyMember3);

            // 스터디 게시글3에 대한 일정 등록
            for (int i = 1; i < 4; i++) {
                StudySchedule testSchedule = new StudySchedule().testSchedule("백준 " + i + "번 문제 발표하기", LocalDate.now());
                testStudyPost3.addStudySchedule(testSchedule);
                em.persist(testSchedule);
            }

            // 스터디 게시글3에 대한 신청 멤버(hji1234) 등록
            ApplicationMember testApplicationMember3 = new ApplicationMember(testMember2);
            testStudyPost3.addApplicationMember(testApplicationMember3);
            em.persist(testApplicationMember3);
        }
    }
}