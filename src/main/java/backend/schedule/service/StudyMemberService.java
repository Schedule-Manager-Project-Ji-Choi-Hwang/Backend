package backend.schedule.service;


import backend.schedule.dto.studymember.StudyMemberResDto;
import backend.schedule.entity.Member;
import backend.schedule.entity.StudyMember;
import backend.schedule.entity.StudyPost;
import backend.schedule.enumlist.ConfirmAuthor;
import backend.schedule.enumlist.ErrorMessage;
import backend.schedule.repository.StudyMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudyMemberService {

    private final StudyMemberRepository studyMemberRepository;


    public void save(Member member, StudyPost studyPost) {
        StudyMember studyMember = new StudyMember(member, studyPost, ConfirmAuthor.MEMBER);
        studyPost.addStudyMember(studyMember);
        studyMemberRepository.save(studyMember);
    }

    public StudyMember findById(Long StudyMemberId) {
        Optional<StudyMember> optionalStudyMember = studyMemberRepository.findById(StudyMemberId);

        return optionalStudyMember.orElseThrow(() -> new IllegalArgumentException(ErrorMessage.STUDY));
    }

    public StudyMember findByMemberAndStudyPost(Long memberId, Long studyBoardId, ConfirmAuthor confirmAuthor) {
        Optional<StudyMember> optionalStudyMember = studyMemberRepository.findByMemberAndStudyPost(memberId, studyBoardId, confirmAuthor);

        return optionalStudyMember.orElseThrow(() -> new IllegalArgumentException(ErrorMessage.AUTHORITY));
    }

    public List<Long> findStudyPostIds(Long memberId) {
        List<Long> studyPostIds = studyMemberRepository.MainPageStudyMembers(memberId).stream().map(s -> s.getStudyPost().getId())
                .collect(Collectors.toList());

        return studyPostIds;
    }

    public void delete(StudyPost studyPost, StudyMember studyMember) {
        studyPost.removeStudyMember(studyMember);
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

        return null;
    }

}
