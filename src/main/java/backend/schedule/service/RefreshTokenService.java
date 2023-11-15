package backend.schedule.service;

import backend.schedule.entity.RefreshToken;
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
        RefreshToken createRefreshToken = RefreshToken.builder()
                .token(refreshToken)
                .memberId(memberId)
                .build();
        refreshTokenRepository.save(createRefreshToken);
    }

    /**
     * (Access 토큰 재발급)
     * 요청 헤더의 Refresh토큰과 DB Refresh토큰 일치 여부 확인
     */
    public boolean matches(String refreshToken, Long memberId, String secretKey) {
        Optional<RefreshToken> findRefreshToken = refreshTokenRepository.findByMemberId(memberId);

        if (findRefreshToken.isEmpty()) {
            return false;
        }

        RefreshToken savedRefreshToken = findRefreshToken.get();

        if (JwtTokenUtil.isExpired(refreshToken, secretKey)) {
            refreshTokenRepository.delete(savedRefreshToken);
            return false;
        }

        if (!savedRefreshToken.getToken().equals(refreshToken)) {
            return false;
        }
        return true;
    }
}
