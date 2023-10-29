package backend.schedule.repository;

import backend.schedule.dto.QStudyPostResponseDto;
import backend.schedule.dto.SearchPostCondition;
import backend.schedule.dto.StudyPostResponseDto;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

import static backend.schedule.entity.QStudyPost.studyPost;
import static org.springframework.util.StringUtils.hasText;


@Repository
public class StudyPostRepositoryImpl implements StudyPostRepositoryCustom {

    private final EntityManager em;
    private final JPAQueryFactory query;

    public StudyPostRepositoryImpl(EntityManager em) {
        this.em = em;
        this.query = new JPAQueryFactory(em);
    }

    /**
     * 스터디 게시판 검색 쿼리
     */
    @Override
    public Slice<StudyPostResponseDto> searchPost(Long lastPostId, SearchPostCondition condition, Pageable pageable) {
        List<StudyPostResponseDto> results =
                query.select(new QStudyPostResponseDto(studyPost.id, studyPost.studyName,
                        studyPost.tag, studyPost.period, studyPost.recruitMember,
                        studyPost.onOff, studyPost.area, studyPost.post))
                .from(studyPost)
                .where(ltPostId(lastPostId),
                        searchCondition(condition.getStudyName()))
                .orderBy(studyPost.id.desc())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        return checkLastPage(pageable, results);
    }

    private BooleanExpression ltPostId(Long lastPostId) {
        return lastPostId == null ? null : studyPost.id.lt(lastPostId);
    }

    private BooleanExpression searchCondition(String studyName) {
        return hasText(studyName) ? studyPost.studyName.contains(studyName) : null;
    }

    private Slice<StudyPostResponseDto> checkLastPage(Pageable pageable, List<StudyPostResponseDto> results) {
        boolean hasNext = false;

        System.out.println("re " + results.size());
        System.out.println("pp " + pageable.getPageSize());

        if (results.size() > pageable.getPageSize()) {
            hasNext = true;
            results.remove(pageable.getPageSize());
        }

        return new SliceImpl<>(results, pageable, hasNext);
    }
}
