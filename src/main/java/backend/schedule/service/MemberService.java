package backend.schedule.service;

import backend.schedule.dto.member.MemberFindPasswordReqDto;
import backend.schedule.dto.member.MemberJoinReqDto;
import backend.schedule.dto.member.MemberLoginReqDto;
import backend.schedule.dto.member.MemberChangePasswordReqDto;
import backend.schedule.entity.Member;
import backend.schedule.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Optional;
import java.util.Random;

import static backend.schedule.enumlist.ErrorMessage.*;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder encoder;
    private final JavaMailSender javaMailSender;

    /**
     * (회원가입)
     * loginId 중복 체크
     */
    public boolean checkLoginIdDuplicate(String loginId) {
        // 로그인 아이디 중복 체크
        if (memberRepository.existsByLoginId(loginId)) {
            throw new IllegalArgumentException(DUPLICATELOGINID);
        }
        return false;
    }

    /**
     * (회원가입)
     * nickname 중복 체크
     */
    public boolean checkNicknameDuplicate(String nickname) {
        // 닉네임 중복 체크
        if (memberRepository.existsByNickname(nickname)) {
            throw new IllegalArgumentException(DUPLICATENICKNAME);
        }
        return false;
    }

    /**
     * (회원가입)
     * 회원 저장 (비밀번호 암호화)
     */
    public Long join(MemberJoinReqDto memberJoinReqDto) {
        // 비밀번호 암호화
        String encodedPassword = encoder.encode(memberJoinReqDto.getPassword());

        // 비밀번호 저장 및 id값 반환
        return memberRepository.save(new Member(memberJoinReqDto, encodedPassword)).getId();
    }

    /**
     * (로그인)
     * 로그인 아이디 이용 멤버 식별 및 비밀번호 일치 여부 확인
     */
    public Member login(MemberLoginReqDto memberLoginReqDto) {
        // 로그인 아이디 이용 멤버 조회
        Member findMember = getLoginMemberByLoginId(memberLoginReqDto.getLoginId());

        // 비밀번호 비교
        if (!encoder.matches(memberLoginReqDto.getPassword(), findMember.getPassword())) {
            throw new IllegalArgumentException(LOGINFAIL);
        }
        return findMember;
    }

    /**
     * (토큰 정보 이용 멤버 식별)
     * 로그인 아이디 이용 멤버 식별
     */
    public Member getLoginMemberByLoginId(String loginId) {
        // 로그인 아이디 이용해 멤버 조회
        Optional<Member> optionalMember = memberRepository.findByLoginId(loginId);

        return optionalMember.orElseThrow(() -> new IllegalArgumentException(MEMBER));
    }

    /**
     * (아이디 찾기)
     * 이메일 이용 멤버 식별
     */
    public Member findMemberByEmail(String email) {
        // 이메일 이용해 멤버 조회
        Optional<Member> optionalMember = memberRepository.findByEmail(email);

        return  optionalMember.orElseThrow(() -> new IllegalArgumentException(MEMBER));
    }

    /**
     * (비밀번호 찾기)
     * 임시 비밀번호 발급 및 이메일 발송
     */
    public void sendMail(MemberFindPasswordReqDto memberFindPasswordReqDto, Member findMember) {
        // 임시 비밀번호 생성
        String tempPassword = createTempPassword();

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        // 비밀번호 변경 (임시 비밀번호)(비밀번호 암호화)
        findMember.changePassword(encoder.encode(tempPassword));

        try {
            // 이메일 설정
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            mimeMessageHelper.setTo(memberFindPasswordReqDto.getEmail());
            mimeMessageHelper.setSubject("[일정관리 앱] 임시 비밀번호 발급");
            mimeMessageHelper.setText(tempPassword);

            // 이메일 발송
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new IllegalArgumentException(SENDEMAILFAIL);
        }
    }

    /**
     * (회원 정보 변경)
     * 비밀번호 변경 (비밀번호 암호화)
     */
    public void changePW(Member member, MemberChangePasswordReqDto memberChangePasswordReqDto) {
        // 멤버 비밀번호 변경(비밀번호 암호화)
        member.changePassword(encoder.encode(memberChangePasswordReqDto.getPassword()));
    }

    /**
     * (회원 정보 변경)
     * 로그인 아이디 및 이메일 정보로 멤버 식별
     */
    public Member findMemberByLoginIdAndEmail(String loginId, String email) {
        // 로그인 아이디 및 이메일 이용해 멤버 조회
        Optional<Member> findMember = memberRepository.findByLoginIdAndEmail(loginId, email);

        return findMember.orElseThrow(() -> new IllegalArgumentException(MEMBER));
    }

    /**
     * (회원 탈퇴)
     * 회원 삭제
     */
    public void deleteMember(Member member) {
        memberRepository.delete(member);
    }

    // 임시 비밀번호 생성 메서드
    public String createTempPassword() {
        Random random = new Random();
        StringBuffer tempPassword = new StringBuffer();

        for (int i = 0; i < 8; i++) {
            int index = random.nextInt(4);

            switch (index) {
                case 0:
                    tempPassword.append((char) ((int) random.nextInt(26) + 97));
                    break;
                case 1:
                    tempPassword.append((char) ((int) random.nextInt(26) + 65));
                    break;
                default:
                    tempPassword.append(random.nextInt(9));
            }
        }
        return tempPassword.toString();
    }
}
