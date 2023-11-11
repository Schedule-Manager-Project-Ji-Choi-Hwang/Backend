package backend.schedule.service;


import backend.schedule.entity.Member;
import backend.schedule.entity.StudyMember;
import backend.schedule.entity.StudyPost;
import backend.schedule.enumlist.ConfirmAuthor;
import backend.schedule.repository.StudyMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StudyMemberService {

    private final StudyMemberRepository studyMemberRepository;

    public void save(Member member, StudyPost studyPost) {
        StudyMember studyMember = new StudyMember(member, studyPost, ConfirmAuthor.MEMBER);
        studyPost.addStudyMember(studyMember);
        studyMemberRepository.save(studyMember);
    }

    public Optional<StudyMember> findById(Long id) {
        return studyMemberRepository.findById(id);
    }

    public void delete(StudyMember studyMember) {
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

}
