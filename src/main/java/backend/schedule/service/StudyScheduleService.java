package backend.schedule.service;


import backend.schedule.dto.studyschedule.StudyScheduleDto;
import backend.schedule.entity.StudySchedule;
import backend.schedule.repository.StudyScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StudyScheduleService {

    private final StudyScheduleRepository studyScheduleRepository;

    public StudySchedule save(StudyScheduleDto studyScheduleDto) {
        StudySchedule studySchedule = new StudySchedule(studyScheduleDto);

        return studyScheduleRepository.save(studySchedule);
    }

    public StudySchedule findById(Long id) {
        Optional<StudySchedule> optionalStudySchedule = studyScheduleRepository.findById(id);

        return optionalStudySchedule.orElse(null);
    }

    public void delete(StudySchedule schedule) {
        studyScheduleRepository.delete(schedule);
    }
}
