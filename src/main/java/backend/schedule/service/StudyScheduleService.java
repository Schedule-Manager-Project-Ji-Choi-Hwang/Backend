package backend.schedule.service;


import backend.schedule.dto.studyschedule.StudyScheduleDto;
import backend.schedule.entity.StudyPost;
import backend.schedule.entity.StudySchedule;
import backend.schedule.enumlist.ErrorMessage;
import backend.schedule.repository.StudyScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StudyScheduleService {

    private final StudyScheduleRepository studyScheduleRepository;

    @Transactional
    public void save(StudyScheduleDto studyScheduleDto, StudyPost findPost) {
        StudySchedule studySchedule = new StudySchedule(studyScheduleDto);
        findPost.addStudySchedule(studySchedule);
        studyScheduleRepository.save(studySchedule);
    }

    public StudySchedule findById(Long id) {
        Optional<StudySchedule> optionalStudySchedule = studyScheduleRepository.findById(id);

        return optionalStudySchedule.orElseThrow(() -> new IllegalArgumentException(ErrorMessage.SCHEDULE));
    }

    @Transactional
    public void studyScheduleUpdate(Long studyScheduleId, StudyScheduleDto scheduleDto) {
        StudySchedule findStudySchedule = findById(studyScheduleId);
        findStudySchedule.updateSchedule(scheduleDto);
    }

    public String removeStudySchedule(Long studyBoardId, Long studyScheduleId) {
        int removeStudySchedule = studyScheduleRepository.removeStudySchedule(studyBoardId, studyScheduleId);

        if (removeStudySchedule == 1) {
            return ErrorMessage.DELETE;
        } else {
            throw new IllegalArgumentException(ErrorMessage.NOTDELETE);
        }
    }

    public void delete(StudySchedule schedule) {
        studyScheduleRepository.delete(schedule);
    }
}
