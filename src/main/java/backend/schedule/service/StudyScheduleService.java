package backend.schedule.service;


import backend.schedule.entity.StudySchedule;
import backend.schedule.repository.StudyScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StudyScheduleService {

    private final StudyScheduleRepository studyScheduleRepository;

    public void save(StudySchedule studySchedule) {
        studyScheduleRepository.save(studySchedule);
    }

    public Optional<StudySchedule> findById(Long id) {
        return studyScheduleRepository.findById(id);
    }

    public void delete(StudySchedule schedule) {
        studyScheduleRepository.delete(schedule);
    }

}
