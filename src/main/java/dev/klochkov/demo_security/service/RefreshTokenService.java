package dev.klochkov.demo_security.service;

import dev.klochkov.demo_security.entity.RefreshToken;
import dev.klochkov.demo_security.entity.User;
import dev.klochkov.demo_security.exception.TokenRefreshException;
import dev.klochkov.demo_security.repository.RefreshTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserService userService;

    public RefreshToken createRefreshToken(User user, String refreshTokenStr, Date expiryDate) {

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(refreshTokenStr)
                .expiryDate(expiryDate.toInstant())
                .build();
        return refreshTokenRepository.save(refreshToken);
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException(token.getToken(), "Refresh token expired. Please make a new sign-in request");
        }
        return token;
    }

    @Transactional
    public void removeByUser(String username) {
        User user = userService.getByUsername(username);
        refreshTokenRepository.deleteByUser(user);
    }
}
