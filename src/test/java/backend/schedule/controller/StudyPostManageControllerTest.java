package backend.schedule.controller;

import backend.schedule.entity.Member;
import backend.schedule.entity.QMember;
import backend.schedule.service.StudyPostService;
import backend.schedule.service.StudyScheduleService;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

@SpringBootTest
@Transactional
class StudyPostManageControllerTest {

    @Autowired
    StudyPostService studyPostService;

    @Autowired
    StudyScheduleService studyScheduleService;

    @Autowired
    EntityManager em;

    @Test
    void test() {
        Member member = new Member("1111", "222");
        em.persist(member);

        JPAQueryFactory query = new JPAQueryFactory(em);
        QMember qMember = new QMember("m");

        Member result = query.selectFrom(qMember).fetchOne();

        Assertions.assertThat(result).isEqualTo(member);
    }
}