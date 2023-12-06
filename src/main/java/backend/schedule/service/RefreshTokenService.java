package backend.schedule.service;

import backend.schedule.entity.RefreshToken;
import backend.schedule.enumlist.ErrorMessage;
import backend.schedule.jwt.JwtTokenUtil;
import backend.schedule.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * (로그인)
     * Refresh 토큰 저장
     */
    public void save(String refreshToken, Long memberId) {
        // Refresh 토큰 객체 생성
        RefreshToken createRefreshToken = new RefreshToken(memberId, refreshToken);

        // Refresh 토큰 객체 저장
        refreshTokenRepository.save(createRefreshToken);
    }

    public void checkRefreshTokenDuplicate(Long memberId) {
        Optional<RefreshToken> optionalRefreshToken = refreshTokenRepository.findByMemberId(memberId);

        if (optionalRefreshToken.isPresent()) {
            RefreshToken findRefreshToken = optionalRefreshToken.get();
            refreshTokenRepository.delete(findRefreshToken);
        }
    }

    /**
     * (Access 토큰 재발급)
     * 요청 헤더의 Refresh토큰과 DB Refresh토큰 일치 여부 확인
     */
    public boolean matches(String requestRefreshToken, Long memberId, String secretKey) {
        // Refresh 토큰 조회
        Optional<RefreshToken> optionalRefreshToken = refreshTokenRepository.findByMemberId(memberId);

        if (optionalRefreshToken.isPresent()) {
            // Refresh 토큰 객체 획득
            RefreshToken savedRefreshToken = optionalRefreshToken.get();

            // 요청 Refresh 토큰 만료기한 검사
            if (JwtTokenUtil.isExpired(requestRefreshToken, secretKey)) {
                refreshTokenRepository.delete(savedRefreshToken);
                throw new IllegalArgumentException(ErrorMessage.TOKENEXPIRE);
            }

            // 요청 Refresh 토큰과 DB Refresh 토큰 비교
            if (!savedRefreshToken.getToken().equals(requestRefreshToken)) {
                throw new IllegalArgumentException(ErrorMessage.TOKEN);
            }
        }
        return true;
    }
}
