package backend.schedule.repository;

import backend.schedule.dto.studypost.QStudyPostResDto;
import backend.schedule.dto.studypost.StudyPostResDto;
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
     * 조건: 스터디 이름 검색 조건
     */
    @Override
    public Slice<StudyPostResDto> searchPost(Long lastPostId, String studyName, Pageable pageable) {
        List<StudyPostResDto> results =
                query.select(new QStudyPostResDto(studyPost.id, studyPost.studyName,
                                studyPost.tag, studyPost.recruitMember, studyPost.studyMembers.size(),
                                studyPost.onOff, studyPost.area, studyPost.post))
                        .from(studyPost)
                        .where(ltPostId(lastPostId),
                                searchCondition(studyName))
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

    private Slice<StudyPostResDto> checkLastPage(Pageable pageable, List<StudyPostResDto> results) {
        boolean hasNext = false;

        if (results.size() > pageable.getPageSize()) {
            hasNext = true;
            results.remove(pageable.getPageSize());
        }

        return new SliceImpl<>(results, pageable, hasNext);
    }
}
