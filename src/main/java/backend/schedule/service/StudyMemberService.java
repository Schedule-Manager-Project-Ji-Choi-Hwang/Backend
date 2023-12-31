package backend.schedule.service;


import backend.schedule.dto.studymember.StudyMemberResDto;
import backend.schedule.dto.studypost.StudyMemberToPostReqDto;
import backend.schedule.dto.studypost.StudyPostNameResDto;
import backend.schedule.entity.Member;
import backend.schedule.entity.StudyMember;
import backend.schedule.entity.StudyPost;
import backend.schedule.enumlist.ConfirmAuthor;
import backend.schedule.enumlist.ErrorMessage;
import backend.schedule.repository.StudyMemberRepository;
import backend.schedule.repository.StudyPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static backend.schedule.enumlist.ConfirmAuthor.LEADER;
import static backend.schedule.enumlist.ConfirmAuthor.MEMBER;

@Service
@Transactional
@RequiredArgsConstructor
public class StudyMemberService {

    private final StudyMemberRepository studyMemberRepository;
    private final StudyPostRepository studyPostRepository;


    public void save(Member member, StudyPost studyPost) {
        if (studyPost.getStudyMembers().size() == studyPost.getRecruitMember()) {
            throw new IllegalArgumentException(ErrorMessage.MEMBERFULL);
        }
        StudyMember studyMember = new StudyMember(member, MEMBER);
        studyPost.addStudyMember(studyMember);
        studyMemberRepository.save(studyMember);
    }

    public boolean myAuthority(Member member, StudyPost studyPost, ConfirmAuthor confirmAuthor) {
        return studyMemberRepository.existsByMemberAndStudyPostAndAndConfirmAuthor(member, studyPost, confirmAuthor);
    }

    public StudyMember studyMemberSearch(Long memberId, Long studyBoardId, ConfirmAuthor confirmAuthor) {
        Optional<StudyMember> optionalStudyMember = studyMemberRepository.studyMemberSearch(memberId, studyBoardId, confirmAuthor);

        return optionalStudyMember.orElseThrow(() -> new IllegalArgumentException(ErrorMessage.AUTHORITY));
    }

    public StudyMember studyMemberSearchNoAuthority(Long memberId, Long studyBoardId) {
        Optional<StudyMember> optionalStudyMember = studyMemberRepository.studyMemberSearchNoAuthority(memberId, studyBoardId);

        return optionalStudyMember.orElseThrow(() -> new IllegalArgumentException(ErrorMessage.AUTHORITY));
    }

    public StudyMember studyMemberGetStudyPost(Long memberId, Long studyBoardId) {
        Optional<StudyMember> optionalStudyMember = studyMemberRepository.studyMemberGetStudyPost(memberId, studyBoardId);

        return optionalStudyMember.orElseThrow(() -> new IllegalArgumentException(ErrorMessage.STUDY));
    }

    public void deleteStudyMember(StudyMember studyMember) {

        if (studyMember.getConfirmAuthor() == LEADER) {
            if (studyMember.getStudyPost().getStudyMembers().size() == 1) {
                studyPostRepository.delete(studyMember.getStudyPost());
            } else {
                StudyMember secondStudyMember = studyMember.getStudyPost().getStudyMembers().get(1);
                secondStudyMember.changeLeader();
                studyMemberRepository.delete(studyMember);
            }
        } else {
            studyMemberRepository.delete(studyMember);
        }
    }

    public List<Long> findStudyPostIds(Long memberId) {
        List<Long> studyPostIds = studyMemberRepository.MainPageStudyMembers(memberId).stream()
                .map(s -> s.getStudyPost().getId())
                .collect(Collectors.toList());

        return studyPostIds;
    }

    public List<StudyMemberToPostReqDto> myPostList(Long memberId) {
        return studyMemberRepository.myPostList(memberId, LEADER).stream()
                .map(StudyMemberToPostReqDto::new)
                .collect(Collectors.toList());
    }

    public List<StudyMemberToPostReqDto> memberPostList(Long memberId) {
        return studyMemberRepository.memberPostList(memberId).stream()
                .map(StudyMemberToPostReqDto::new)
                .collect(Collectors.toList());
    }

    public List<StudyPostNameResDto> myPostNameList(Long memberId) {
        return studyMemberRepository.myPostList(memberId, LEADER).stream()
                .map(StudyPostNameResDto::new)
                .collect(Collectors.toList());
    }

    public void delete(StudyMember studyMember) {
//        studyPost.removeStudyMember(studyMember);
        studyMemberRepository.delete(studyMember);
    }

    public List<StudyMemberResDto> findStudyMembers(StudyPost studyPost) {
        List<StudyMemberResDto> studyMemberResDtos = studyPost.getStudyMembers().stream()
                .map(StudyMemberResDto::new)
                .collect(Collectors.toList());
        return studyMemberResDtos;
    }

    public List<StudyMember> findStudyMembersWithdrawal(Long memberId) {
        return studyMemberRepository.findStudyMembersWithdrawal(memberId);
    }

    public void StudyMembersWithdrawal(List<StudyMember> studyMembers) {
        for (StudyMember studyMember : studyMembers) {
            deleteStudyMember(studyMember);
        }
    }

    public String expulsionStudyMember(Long studyBoardId, Long studyMemberId) {
        int deleteStudyMember = studyMemberRepository.deleteStudyMember(studyBoardId, studyMemberId, MEMBER);

        if (deleteStudyMember == 1) {
            return ErrorMessage.EXPULSIONSTUDYMEMBER;
        } else {
            throw new IllegalArgumentException(ErrorMessage.STUDY);
        }
    }

}
