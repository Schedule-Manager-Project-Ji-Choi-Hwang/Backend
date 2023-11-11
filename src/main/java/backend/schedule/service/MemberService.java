package backend.schedule.service;

import backend.schedule.dto.EmailMessageDto;
import backend.schedule.dto.FindLoginIdResDto;
import backend.schedule.dto.MemberJoinDto;
import backend.schedule.dto.MemberLoginDto;
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

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder encoder;

    private final JavaMailSender javaMailSender;

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

    public boolean checkLoginIdAndEmail(String loginId, String email) {
        return memberRepository.existsByLoginIdAndEmail(loginId, email);
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

    public FindLoginIdResDto findLoginId(String email) {
        Optional<Member> optionalMember = memberRepository.findByEmail(email);

        if (optionalMember.isEmpty()) {
            return FindLoginIdResDto.builder()
                    .loginId(null)
                    .message("해당 이메일로 가입된 아이디가 없습니다.")
                    .build();
        }

        Member savedMember = optionalMember.get();

        return FindLoginIdResDto.builder()
                .loginId(savedMember.getLoginId())
                .message(null)
                .build();
    }

    public String sendMail(EmailMessageDto emailMessageDto, String type) {
        String tempPassword = createTempPassword();

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        if (type.equals("password")) {
            Optional<Member> optionalMember = memberRepository.findByEmail(emailMessageDto.getTo());
            if (optionalMember.isEmpty()) {
                return "fail";
            } else {
                Member findMember = optionalMember.get();
                findMember.changePassword(encoder.encode(tempPassword));
            }
        }

        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            mimeMessageHelper.setTo(emailMessageDto.getTo());
            mimeMessageHelper.setSubject(emailMessageDto.getSubject());
            mimeMessageHelper.setText(tempPassword);
            javaMailSender.send(mimeMessage);

            log.info("이메일 발송 성공");

            return "success";
        } catch (MessagingException e) {
            log.info("이메일 발송 실패");
            throw new RuntimeException(e);
        }
    }

    public String createTempPassword() {
        Random random = new Random();
        StringBuffer tempPassword = new StringBuffer();

        for (int i=0; i < 8; i++) {
            int index = random.nextInt(4);

            switch (index) {
                case 0: tempPassword.append((char) ((int) random.nextInt(26) + 97)); break;
                case 1: tempPassword.append((char) ((int) random.nextInt(26) + 65)); break;
                default: tempPassword.append(random.nextInt(9));
            }
        }
        return tempPassword.toString();
    }
}
