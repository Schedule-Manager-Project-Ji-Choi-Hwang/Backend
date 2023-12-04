package backend.schedule.service;


import backend.schedule.dto.studymember.StudyMemberResDto;
import backend.schedule.dto.studypost.StudyMemberToPostReqDto;
import backend.schedule.entity.Member;
import backend.schedule.entity.StudyMember;
import backend.schedule.entity.StudyPost;
import backend.schedule.enumlist.ConfirmAuthor;
import backend.schedule.enumlist.ErrorMessage;
import backend.schedule.repository.StudyMemberRepository;
import backend.schedule.repository.StudyPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static backend.schedule.enumlist.ConfirmAuthor.LEADER;

@Service
@RequiredArgsConstructor
public class StudyMemberService {

    private final StudyMemberRepository studyMemberRepository;
    private final StudyPostRepository studyPostRepository;


    public void save(Member member, StudyPost studyPost) {
        StudyMember studyMember = new StudyMember(member, ConfirmAuthor.MEMBER);
        studyPost.addStudyMember(studyMember);
        studyMemberRepository.save(studyMember);
    }

    public StudyMember findById(Long StudyMemberId) {
        Optional<StudyMember> optionalStudyMember = studyMemberRepository.findById(StudyMemberId);

        return optionalStudyMember.orElseThrow(() -> new IllegalArgumentException(ErrorMessage.STUDY));
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
                studyPostRepository.delete(studyMember.getStudyPost()); // 추후에 연관관계 더 세팅하고 삭제되는지 확인(cascade or 연관관계 관련된거 다 딜리트)
            } else {
                StudyMember secondStudyMember = studyMember.getStudyPost().getStudyMembers().get(1); // 2번째 멤버 찾아오기
                secondStudyMember.changeLeader(); // 2번째 멤버 리더권한 양도
                studyMemberRepository.delete(studyMember); // 원래 리더는 탈퇴
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

    public void delete(StudyMember studyMember) {
//        studyPost.removeStudyMember(studyMember);
        studyMemberRepository.delete(studyMember);
    }

    public List<StudyMemberResDto> findStudyMembers(Long studyboardId) {
        Optional<StudyPost> optionalStudyPost = studyMemberRepository.studyMembersByStudyboardId(studyboardId);

        if (optionalStudyPost.isPresent()) {
            StudyPost studyPost = optionalStudyPost.get();
            List<StudyMemberResDto> studyMemberResDtos = studyPost.getStudyMembers().stream()
                    .map(StudyMemberResDto::new)
                    .collect(Collectors.toList());
            return studyMemberResDtos;
        }

        throw new IllegalArgumentException(ErrorMessage.STUDY);
    }

    public List<StudyMember> findStudyMembersWithdrawal(Long memberId) {
        return studyMemberRepository.findStudyMembersWithdrawal(memberId);
    }

    public void StudyMembersWithdrawal(List<StudyMember> studyMembers) {
        for (StudyMember studyMember : studyMembers) {
            deleteStudyMember(studyMember);
        }
    }

}
