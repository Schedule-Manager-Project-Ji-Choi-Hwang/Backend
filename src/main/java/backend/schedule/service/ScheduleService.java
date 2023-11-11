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

    public Schedule add(ScheduleReqDto scheduleReqDto) {
        Optional<PersonalSubject> optionalPersonalSubject = personalSubjectRepository.findBySubjectName(scheduleReqDto.getSubjectName());
        if (optionalPersonalSubject.isPresent()) {
            PersonalSubject personalSubject = optionalPersonalSubject.get();
            Schedule schedule = new Schedule(scheduleReqDto.getScheduleName(), scheduleReqDto.getPeriod());
            schedule.setPersonalSubject(personalSubject);
            scheduleRepository.save(schedule);
            return schedule;
        } else {
            return null;
        }
    }

    public Schedule findOne(Long scheduleId) {
        Optional<Schedule> optionalSchedule = scheduleRepository.findById(scheduleId);
        if (optionalSchedule.isPresent()) {
            return optionalSchedule.get();
        } else {
            return null;
        }
    }

    public List<Schedule> findSchedulesBySubject(Long subjectId) {
        return scheduleRepository.findAllByPersonalSubject(subjectId);
    }

    public List<ScheduleResDto> findSchedulesByMemberId(Long memberId) {
        Member member = memberRepository.findByIdWithPersonalSubjects(memberId)
                .orElseThrow(() -> new EntityNotFoundException("Member not found"));

        List<ScheduleResDto> scheduleResDtos = new ArrayList<>();

        List<PersonalSubject> personalSubjects = memberRepository.findPersonalSubjectsWithSchedulesByMemberId(memberId);
        for (PersonalSubject personalSubject: personalSubjects) {
            String subjectName = personalSubject.getSubjectName();
            for (Schedule schedule: personalSubject.getSchedules()) {
                ScheduleResDto schedulesResDto = new ScheduleResDto(subjectName, schedule.getScheduleName(), schedule.getPeriod());
                scheduleResDtos.add(schedulesResDto);
            }
        }

        return scheduleResDtos;
    }

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

    public void deleteSchedule(Long scheduleId) {
        scheduleRepository.deleteById(scheduleId);
    }
}
