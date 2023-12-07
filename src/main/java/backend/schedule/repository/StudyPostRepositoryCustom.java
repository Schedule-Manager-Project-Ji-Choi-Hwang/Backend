package backend.schedule.repository;

import backend.schedule.dto.studypost.StudyPostResDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface StudyPostRepositoryCustom {

    Slice<StudyPostResDto> searchPost(Long lastPostId, String studyName, Pageable pageable);
}
