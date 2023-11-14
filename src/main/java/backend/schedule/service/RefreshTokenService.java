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

    public void save(String refreshToken, Long memberId) {
        RefreshToken createRefreshToken = RefreshToken.builder()
                                            .token(refreshToken)
                                            .memberId(memberId)
                                            .build();
        refreshTokenRepository.save(createRefreshToken);
    }

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
