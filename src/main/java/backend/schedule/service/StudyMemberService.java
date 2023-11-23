package backend.schedule.service;


import backend.schedule.dto.StudyMemberResDto;
import backend.schedule.entity.Member;
import backend.schedule.entity.StudyMember;
import backend.schedule.entity.StudyPost;
import backend.schedule.enumlist.ConfirmAuthor;
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

    public List<Long> findStudyPostIds(Long memberId) {
        List<Long> studyPostIds = studyMemberRepository.test12(memberId).stream().map(s -> s.getStudyPost().getId())
                .collect(Collectors.toList());

        return studyPostIds;
    }


    public void save(Member member, StudyPost studyPost) {
        StudyMember studyMember = new StudyMember(member, studyPost, ConfirmAuthor.MEMBER);
        studyPost.addStudyMember(studyMember);
        studyMemberRepository.save(studyMember);
    }

    public StudyMember findById(Long StudyMemberId) {
        Optional<StudyMember> optionalStudyMember = studyMemberRepository.findById(StudyMemberId);

        return optionalStudyMember.orElse(null);
    }

    public void delete(StudyPost studyPost, StudyMember studyMember) {
        studyPost.removeStudyMember(studyMember);
        studyMemberRepository.delete(studyMember);
    }

    public StudyMember findByMemberAndStudyPost(Long memberId, Long studyBoardId) {
        Optional<StudyMember> optionalStudyMember = studyMemberRepository.findByMemberAndStudyPost(memberId, studyBoardId,
                ConfirmAuthor.LEADER);
        if (optionalStudyMember.isPresent()) {
            StudyMember studyMember = optionalStudyMember.get();
            return studyMember;
        } else {
            return null;
        }
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
