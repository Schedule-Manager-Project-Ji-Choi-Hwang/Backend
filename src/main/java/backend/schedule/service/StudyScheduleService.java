package backend.schedule.service;


import backend.schedule.dto.studyschedule.StudyPostScheduleSetDto;
import backend.schedule.dto.studyschedule.StudyScheduleEditReqDto;
import backend.schedule.dto.studyschedule.StudyScheduleReqDto;
import backend.schedule.entity.StudyPost;
import backend.schedule.entity.StudySchedule;
import backend.schedule.enumlist.ErrorMessage;
import backend.schedule.repository.StudyMemberRepository;
import backend.schedule.repository.StudyScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StudyScheduleService {

    private final StudyScheduleRepository studyScheduleRepository;
    private final StudyMemberRepository studyMemberRepository;

    @Transactional
    public void addStudySchedule(StudyScheduleReqDto scheduleReqDto, StudyPost studyPost) {

        if (scheduleReqDto.getPeriod() != null) { // 단일 등록
            StudySchedule studySchedule = new StudySchedule(scheduleReqDto, scheduleReqDto.getPeriod());
            studyPost.addStudySchedule(studySchedule);
            studyScheduleRepository.save(studySchedule);

        } else { // 반복 등록
            LocalDate nextDate = scheduleReqDto.getStartDate(); // 저장될 날짜를 가지고 있는 놈. for문의 i 변수같은 존재.

            StudySchedule startStudySchedule = new StudySchedule(scheduleReqDto, nextDate);
            studyPost.addStudySchedule(startStudySchedule);
            studyScheduleRepository.save(startStudySchedule);

            while (!nextDate.isAfter(scheduleReqDto.getEndDate())) { // nextDate가 endDate를 지났는가? (서로 날짜가 같으면 통과해버림. 지나야 반복 종료됨)

                switch (scheduleReqDto.getRepeat()) {
                    case "DAILY":
                        nextDate = nextDate.plusDays(1); // 하루 +
                        break;
                    case "WEEKLY":
                        nextDate = nextDate.plusWeeks(1); // 일주일 +
                        break;
                    case "MONTHLY":
                        nextDate = nextDate.plusMonths(1); // 한달 +
                        break;
                }

                StudySchedule repeatStudySchedule = new StudySchedule(scheduleReqDto, nextDate);
                studyPost.addStudySchedule(repeatStudySchedule);
                studyScheduleRepository.save(repeatStudySchedule);

                if (nextDate.isEqual(scheduleReqDto.getEndDate())) break; //nextDate == endDate 종료 ex)12-01 == 12-01
            }
        }
    }

    public StudySchedule findSchedule(Long studyBoardId, Long studyScheduleId) {
        Optional<StudySchedule> optionalStudySchedule = studyScheduleRepository.findSchedule(studyBoardId, studyScheduleId);

        return optionalStudySchedule.orElseThrow(() -> new IllegalArgumentException(ErrorMessage.SCHEDULE));
    }

    public List<StudyPostScheduleSetDto> findSchedules(Long memberId, LocalDate date) {
        return studyMemberRepository.findStudymembers(memberId, date).stream()
                .map(StudyPostScheduleSetDto::new)
                .collect(Collectors.toList());
    }



    @Transactional
    public void updateStudySchedule(StudySchedule studySchedule, StudyScheduleEditReqDto scheduleEditReqDto) {
        studySchedule.updateSchedule(scheduleEditReqDto);
    }

    public String removeStudySchedule(Long studyBoardId, Long studyScheduleId) {
        int removeStudySchedule = studyScheduleRepository.removeStudySchedule(studyBoardId, studyScheduleId);

        if (removeStudySchedule == 1) {
            return ErrorMessage.DELETE;
        } else {
            throw new IllegalArgumentException(ErrorMessage.NOTDELETE);
        }
    }

}
