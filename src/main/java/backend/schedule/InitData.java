package backend.schedule;


import backend.schedule.entity.StudyPost;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
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
            for (int i = 0; i < 10000; i++) {
                em.persist(new StudyPost(""+i, LocalDate.now()));
            }
        }
    }
}