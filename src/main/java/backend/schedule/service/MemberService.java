package backend.schedule.service;

import backend.schedule.dto.MemberJoinDto;
import backend.schedule.dto.MemberLoginDto;
import backend.schedule.entity.Member;
import backend.schedule.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder encoder;

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
     * 회원가입 기능
     */
    public void join(MemberJoinDto memberJoinDto) {
        memberRepository.save(memberJoinDto.toEntity(encoder.encode(memberJoinDto.getPassword())));
    }

    /**
     * 로그인 기능
     */
    public Member login(MemberLoginDto memberLoginDto) {
        Optional<Member> optionalMember = memberRepository.findByLoginId(memberLoginDto.getLoginId());

        if (optionalMember.isEmpty()) {
            return null;
        }

        Member member = optionalMember.get();

        if (!encoder.matches(memberLoginDto.getPassword(), member.getPassword())) {
            return null;
        }

        return member;
    }

    public Member getLoginMemberByLoginId(String loginId) {
        if (loginId == null) {
            return null;
        }

        Optional<Member> optionalMember = memberRepository.findByLoginId(loginId);

        return optionalMember.orElse(null);
    }
}
