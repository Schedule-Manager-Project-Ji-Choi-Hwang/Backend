package backend.schedule.service;


import backend.schedule.dto.studypost.SearchPostCondition;
import backend.schedule.dto.studypost.StudyPostDto;
import backend.schedule.dto.studypost.StudyPostFrontSaveDto;
import backend.schedule.dto.studyschedule.StudyPostScheduleSetDto;
import backend.schedule.dto.studypost.StudyPostResDto;
import backend.schedule.entity.Member;
import backend.schedule.entity.StudyMember;
import backend.schedule.entity.StudyPost;
import backend.schedule.enumlist.ConfirmAuthor;
import backend.schedule.repository.StudyMemberRepository;
import backend.schedule.repository.StudyPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StudyPostService {

    private final StudyPostRepository studyPostRepository;
    private final StudyMemberRepository studyMemberRepository;

    public StudyPostScheduleSetDto detailStudySchedules(Long studyboardId, LocalDate date) {
        StudyPost studyPost = studyPostRepository.DetailPageStudySchedules(studyboardId, date);
        return new StudyPostScheduleSetDto(studyPost);
    }

    public StudyPostFrontSaveDto save(StudyPostDto studyPostDto, Member findMember) {
        StudyPost studyPost = new StudyPost(studyPostDto);
        StudyMember studyMember = new StudyMember(findMember, studyPost, ConfirmAuthor.LEADER);

        studyPost.addStudyMember(studyMember);
        StudyPost savedStudyPost = studyPostRepository.save(studyPost);//스터디 멤버 리더 지정
        studyMemberRepository.save(studyMember);

        return new StudyPostFrontSaveDto(savedStudyPost);
    }

    public StudyPost findById(Long id) {
        Optional<StudyPost> optionalStudyPost = studyPostRepository.findById(id);

        return optionalStudyPost.orElse(null);
    }

    public Slice<StudyPostResDto> search(Long lastPostId, SearchPostCondition condition, Pageable pageable) {
        return studyPostRepository.searchPost(lastPostId, condition, pageable);
    }

    public void delete(Long id) {
        studyPostRepository.deleteById(id);
    }

    public StudyPost studyScheduleList(Long id) {
        return studyPostRepository.studyScheduleList(id);
    }

    public StudyPost studyAnnouncement(Long boardId, Long id) {
        return studyPostRepository.studyAnnouncement(boardId, id);
    }

    public StudyPost studyAnnouncements(Long boardId) {
        return studyPostRepository.studyAnnouncements(boardId);
    }
}
