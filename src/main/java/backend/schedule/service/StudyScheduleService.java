package backend.schedule.service;


import backend.schedule.dto.StudyScheduleDto;
import backend.schedule.entity.StudySchedule;
import backend.schedule.repository.StudyScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StudyScheduleService {

    private final StudyScheduleRepository studyScheduleRepository;

    public StudySchedule save(StudyScheduleDto studyScheduleDto) {
        StudySchedule studySchedule =
                new StudySchedule(studyScheduleDto);

        return studyScheduleRepository.save(studySchedule);
    }

    public Optional<StudySchedule> findById(Long id) {
        return studyScheduleRepository.findById(id);
    }

    public void delete(StudySchedule schedule) {
        studyScheduleRepository.delete(schedule);
    }

    public List<StudySchedule> findAll() {
        return studyScheduleRepository.findAll();
    }

}
