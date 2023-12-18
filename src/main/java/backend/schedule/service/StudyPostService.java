package backend.schedule.service;


import backend.schedule.dto.studypost.AnnouncementsAndStudyMembersDto;
import backend.schedule.dto.studypost.SearchPostCondition;
import backend.schedule.dto.studypost.StudyPostDto;
import backend.schedule.dto.studyschedule.StudyPostScheduleSetDto;
import backend.schedule.dto.studypost.StudyPostResDto;
import backend.schedule.entity.Member;
import backend.schedule.entity.StudyMember;
import backend.schedule.entity.StudyPost;
import backend.schedule.enumlist.ConfirmAuthor;
import backend.schedule.enumlist.ErrorMessage;
import backend.schedule.repository.StudyMemberRepository;
import backend.schedule.repository.StudyPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

import static backend.schedule.enumlist.ErrorMessage.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StudyPostService {

    private final StudyPostRepository studyPostRepository;
    private final StudyMemberRepository studyMemberRepository;

//    public StudyPostScheduleSetDto detailStudySchedules(Long studyboardId, LocalDate date) {
//        StudyPost studyPost = studyPostRepository.DetailPageStudySchedules(studyboardId, date);
//        return new StudyPostScheduleSetDto(studyPost);
//    }

    @Transactional
    public void save(StudyPostDto studyPostDto, Member member) {
        StudyPost studyPost = new StudyPost(studyPostDto);
        StudyMember studyMember = new StudyMember(member, ConfirmAuthor.LEADER);

        studyPost.addStudyMember(studyMember);
        studyPostRepository.save(studyPost);//스터디 멤버 리더 지정
        studyMemberRepository.save(studyMember);
    }

    public StudyPost findById(Long id) {
        Optional<StudyPost> optionalStudyPost = studyPostRepository.findById(id);

        return optionalStudyPost.orElseThrow(() -> new IllegalArgumentException(POST));
    }

    public Slice<StudyPostResDto> search(Long lastPostId, String studyName, Pageable pageable) {
        return studyPostRepository.searchPost(lastPostId, studyName, pageable);
    }

    @Transactional
    public void delete(StudyPost studyPost) {
        studyPostRepository.delete(studyPost);
    }

    public StudyPost studyAnnouncement(Long boardId, Long id) {
        Optional<StudyPost> optionalStudyPost = studyPostRepository.studyAnnouncement(boardId, id);

        return optionalStudyPost.orElseThrow(() -> new IllegalArgumentException(ANNOUNCEMENT));
    }

    public StudyPost studyAnnouncements(Long boardId) {
        Optional<StudyPost> optionalStudyPost = studyPostRepository.studyAnnouncements(boardId);

        return optionalStudyPost.orElseThrow(() -> new IllegalArgumentException(ANNOUNCEMENT));
    }

    public StudyPost findStudyPostByApplicationMembers(Long studyBoardId) {
        Optional<StudyPost> optionalStudyPost = studyPostRepository.findStudyPostByApplicationMembers(studyBoardId);

        return optionalStudyPost.orElseThrow(() -> new ArrayIndexOutOfBoundsException(APPLICATION));
    }

    public StudyPost returnToStudyMembers(Long studyBoardId) {
        Optional<StudyPost> optionalStudyPost = studyPostRepository.findStudyPostGetStudyMembers(studyBoardId);

        return optionalStudyPost.orElseThrow(()-> new IllegalArgumentException(POST));
    }

    @Transactional
    public void updateStudyPost(StudyPost studyPost, StudyPostDto studyPostDto) {
        studyPost.updatePost(studyPostDto);
    }

    public AnnouncementsAndStudyMembersDto returnToStudyGroupInfo(StudyPost studyPostSa,StudyPost studyPostSm, boolean myAuthority) {
        return new AnnouncementsAndStudyMembersDto(studyPostSa, studyPostSm, myAuthority);
    }

//    public StudyPost studyScheduleList(Long id) {
//        Optional<StudyPost> optionalStudyPost = studyPostRepository.studyScheduleList(id);
//
//        return optionalStudyPost.orElseThrow(() -> new IllegalArgumentException(ErrorMessage.SCHEDULE));
//    }
}
