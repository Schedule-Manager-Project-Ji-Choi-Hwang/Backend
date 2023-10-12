package backend.schedule.service;

import backend.schedule.dto.MemberJoinDto;
import backend.schedule.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder encoder;

    /**
     * 회원가입 시 loginId 중복 체크
     */
    public boolean checkLoginIdDuplicate(String loginId) {
        return memberRepository.existsByLoginId(loginId);
    }

    /**
     * 회원가입 시 nickname 중복 체크
     */
    public boolean checkNicknameDuplicate(String nickname) {
        return memberRepository.existsByNickname(nickname);
    }

    /**
     * 회원가입 기능 (시큐리티 이용 방식으로 변경 필요)
     */
    public void join(MemberJoinDto memberJoinDto) {
        memberRepository.save(memberJoinDto.toEntity(encoder.encode(memberJoinDto.getPassword())));
    }
}
