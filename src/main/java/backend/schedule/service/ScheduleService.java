package backend.schedule.service;

import backend.schedule.dto.schedule.ScheduleEditReqDto;
import backend.schedule.dto.schedule.ScheduleReqDto;
import backend.schedule.dto.schedule.ScheduleResDto;
import backend.schedule.entity.Subject;
import backend.schedule.entity.Schedule;
import backend.schedule.enumlist.ErrorMessage;
import backend.schedule.repository.SubjectRepository;
import backend.schedule.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    private final SubjectRepository subjectRepository;

    /**
     * (스케쥴 저장)
     * 단일 저장 및 반복 저장
     */
    public void addSchedule(ScheduleReqDto scheduleReqDto, Subject subject) {
        if (scheduleReqDto.getPeriod() != null) { // 단일 등록
            log.info("period : {}" ,scheduleReqDto.getPeriod());
            Schedule schedule = new Schedule(scheduleReqDto, scheduleReqDto.getPeriod());
            subject.addSchedules(schedule);
            scheduleRepository.save(schedule);

        } else { // 반복 등록
            LocalDate nextDate = scheduleReqDto.getStartDate(); // 저장될 날짜를 가지고 있는 놈. for문의 i 변수같은 존재.

            Schedule schedule1 = new Schedule(scheduleReqDto, nextDate);
            subject.addSchedules(schedule1);
            scheduleRepository.save(schedule1);

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
                Schedule schedule = new Schedule(scheduleReqDto, nextDate);
                subject.addSchedules(schedule);
                scheduleRepository.save(schedule);

                if (nextDate.isEqual(scheduleReqDto.getEndDate())) break;
            }
        }
    }

    /**
     * (스케쥴 단일 조회)
     * 스케쥴 단일 조회
     */
    public Schedule findScheduleById(Long scheduleId, Long subjectId) {
        Optional<Schedule> optionalSchedule = scheduleRepository.findSchedule(scheduleId, subjectId);

        return optionalSchedule.orElseThrow(() -> new IllegalArgumentException(ErrorMessage.SCHEDULE));
    }

//    /**
//     * (스케쥴 전체 조회)
//     * 스케쥴 전체 조회 (개인 과목 별)
//     */
//    public List<Schedule> findSchedulesBySubject(Long subjectId) {
//        return scheduleRepository.findAllByPersonalSubject(subjectId);
//    }

    /**
     * (스케쥴 전체 조회)
     * 스케쥴 전체 조회 (멤버별)
     */
    public List<ScheduleResDto> findSchedulesByMemberId(Long memberId, LocalDate date) {
        List<ScheduleResDto> scheduleResDtos = subjectRepository.findSubjectsWithSchedulesByMemberId(memberId, date).stream()
                .map(ScheduleResDto::new)
                .collect(Collectors.toList());

        return scheduleResDtos;
    }

    /**
     * (스케쥴 변경)
     * 스케쥴 변경 (제목, 기간(period))
     */
    public void updateSchedule(Schedule schedule, ScheduleEditReqDto scheduleEditReqDto) {
        schedule.changeSchedule(scheduleEditReqDto);
    }

    /**
     * (스케쥴 삭제)
     * 스케쥴 삭제
     */
    public void deleteSchedule(Schedule schedule) {
        scheduleRepository.delete(schedule);
    }
}
