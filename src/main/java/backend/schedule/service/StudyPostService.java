package backend.schedule.service;


import backend.schedule.dto.StudyPostDto;
import backend.schedule.entity.StudyPost;
import backend.schedule.repository.StudyPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StudyPostService {

    private final StudyPostRepository studyPostRepository;

    public void save(StudyPostDto postDto) {
        StudyPost post = StudyPost.builder()
                .studyName(postDto.getStudyName())
                .tag(postDto.getTag())
                .period(postDto.getPeriod())
                .recruitMember(postDto.getRecruitMember())
                .onOff(postDto.isOnOff())
                .area(postDto.getArea())
                .post(postDto.getPost())
                .build();

        studyPostRepository.save(post);
    }

    public Optional<StudyPost> findById(Long id) {
        return studyPostRepository.findById(id);
    }

    public void delete(StudyPost studyPost) {
        studyPostRepository.delete(studyPost);
    }
}
