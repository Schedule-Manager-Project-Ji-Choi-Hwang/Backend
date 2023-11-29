package backend.schedule.jwt;

import backend.schedule.entity.Member;
import backend.schedule.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
@RequiredArgsConstructor
public class JwtTokenExtraction {

    private final MemberService memberService;

    public Member extractionMember(HttpServletRequest request, String mySecretkey) {
        String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION).substring(7);
        String secretKey = mySecretkey;
        String memberLoginId = JwtTokenUtil.getLoginId(accessToken, secretKey);
        return memberService.getLoginMemberByLoginId(memberLoginId);
    }

    public Long extractionMemberId(HttpServletRequest request, String mySecretkey) {
        String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION).substring(7);
        String secretKey = mySecretkey;
        String memberLoginId = JwtTokenUtil.getLoginId(accessToken, secretKey);
        return memberService.getLoginMemberByLoginId(memberLoginId).getId();
    }
}
