package backend.schedule.repository;

import backend.schedule.dto.SearchPostCondition;
import backend.schedule.dto.StudyPostResponseDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface StudyPostRepositoryCustom {

    Slice<StudyPostResponseDto> searchPost(Long lastPostId, SearchPostCondition condition, Pageable pageable);
}
