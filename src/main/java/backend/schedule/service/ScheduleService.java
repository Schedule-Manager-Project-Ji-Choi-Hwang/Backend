package backend.schedule.service;

import backend.schedule.dto.ScheduleReqDto;
import backend.schedule.dto.ScheduleResDto;
import backend.schedule.entity.Member;
import backend.schedule.entity.PersonalSubject;
import backend.schedule.entity.Schedule;
import backend.schedule.repository.MemberRepository;
import backend.schedule.repository.PersonalSubjectRepository;
import backend.schedule.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    private final PersonalSubjectRepository personalSubjectRepository;

    private final MemberRepository memberRepository;

    /**
     * (스케쥴 저장)
     * 단일 저장 및 반복 저장
     */
    public String add(ScheduleReqDto scheduleReqDto) {
        Optional<PersonalSubject> optionalPersonalSubject = personalSubjectRepository.findBySubjectName(scheduleReqDto.getSubjectName());
        if (optionalPersonalSubject.isPresent()) { // 옵셔널 검사 코드
            PersonalSubject personalSubject = optionalPersonalSubject.get();
            if (scheduleReqDto.getPeriod() != null) { // 단일 등록
                Schedule schedule = new Schedule(scheduleReqDto.getScheduleName(), scheduleReqDto.getPeriod());
                schedule.setPersonalSubject(personalSubject);
                scheduleRepository.save(schedule);
                return "성공";
            } else { // 반복 등록
//                List<LocalDate> dates = new ArrayList<>();
                LocalDate nextDate = scheduleReqDto.getStartDate(); // 저장될 날짜를 가지고 있는 놈. for문의 i 변수같은 존재.

                Schedule schedule1 = new Schedule(scheduleReqDto.getScheduleName(), nextDate);
                schedule1.setPersonalSubject(personalSubject);
                scheduleRepository.save(schedule1);

                while (!nextDate.isAfter(scheduleReqDto.getEndDate())) { // nextDate가 endDate를 지났는가? (서로 날짜가 같으면 통과해버림. 지나야 반복 종료됨)
//                    dates.add(nextDate);

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
                    Schedule schedule = new Schedule(scheduleReqDto.getScheduleName(), nextDate);
                    schedule.setPersonalSubject(personalSubject);
                    scheduleRepository.save(schedule);
                    if (nextDate.isEqual(scheduleReqDto.getEndDate())) { // nextDate가 endDate와 같으면 반복 종료시킴.
                        break;
                    }
                }
                return "성공";
            }
        } else {
            return null;
        }
    }

    /**
     * (스케쥴 단일 조회)
     * 스케쥴 단일 조회
     */
    public Schedule findOne(Long scheduleId) {
        Optional<Schedule> optionalSchedule = scheduleRepository.findById(scheduleId);
        if (optionalSchedule.isPresent()) {
            return optionalSchedule.get();
        } else {
            return null;
        }
    }

    /**
     * (스케쥴 전체 조회)
     * 스케쥴 전체 조회 (개인 과목 별)
     */
    public List<Schedule> findSchedulesBySubject(Long subjectId) {
        return scheduleRepository.findAllByPersonalSubject(subjectId);
    }

    /**
     * (스케쥴 전체 조회)
     * 스케쥴 전체 조회 (멤버별)
     */
    public List<ScheduleResDto> findSchedulesByMemberId(Long memberId) {
        Member member = memberRepository.findByIdWithPersonalSubjects(memberId)
                .orElseThrow(() -> new EntityNotFoundException("Member not found"));

        List<ScheduleResDto> scheduleResDtos = new ArrayList<>();

        List<PersonalSubject> personalSubjects = memberRepository.findPersonalSubjectsWithSchedulesByMemberId(memberId);
        for (PersonalSubject personalSubject : personalSubjects) {
            String subjectName = personalSubject.getSubjectName();
            for (Schedule schedule : personalSubject.getSchedules()) {
                ScheduleResDto schedulesResDto = new ScheduleResDto(subjectName, schedule.getScheduleName(), schedule.getPeriod());
                scheduleResDtos.add(schedulesResDto);
            }
        }

        return scheduleResDtos;
    }

    /**
     * (스케쥴 변경)
     * 스케쥴 변경 (제목, 기간(period))
     */
    public Schedule updateSchedule(Long scheduleId, ScheduleReqDto scheduleReqDto) {
        Optional<Schedule> optionalSchedule = scheduleRepository.findById(scheduleId);
        if (optionalSchedule.isPresent()) {
            Schedule schedule = optionalSchedule.get();
            schedule.changeScheduleNameAndPeriod(scheduleReqDto);
            return schedule;
        } else {
            return null;
        }
    }

    /**
     * (스케쥴 삭제)
     * 스케쥴 삭제
     */
    public void deleteSchedule(Long scheduleId) {
        scheduleRepository.deleteById(scheduleId);
    }
}
