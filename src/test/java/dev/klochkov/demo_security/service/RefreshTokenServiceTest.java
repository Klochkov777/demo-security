package dev.klochkov.demo_security.service;

import dev.klochkov.demo_security.entity.RefreshToken;
import dev.klochkov.demo_security.entity.User;
import dev.klochkov.demo_security.exception.TokenRefreshException;
import dev.klochkov.demo_security.repository.RefreshTokenRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RefreshTokenServiceTest {
    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    @Test
    void testCreateRefreshToken() {
        User user = User.builder().username("testuser").build();
        String token = "refreshToken";
        Date expiryDate = Date.from(Instant.now().plusSeconds(1000000000l));
        RefreshToken refreshToken = RefreshToken.builder().user(user).token(token).expiryDate(expiryDate.toInstant()).build();

        when(refreshTokenRepository.save(Mockito.any(RefreshToken.class))).thenReturn(refreshToken);

        RefreshToken createdToken = refreshTokenService.createRefreshToken(user, token, expiryDate);

        assertEquals(refreshToken, createdToken);
        verify(refreshTokenRepository).save(Mockito.any(RefreshToken.class));
    }

    @Test
    void testFindByToken() {
        String token = "refreshToken";
        RefreshToken refreshToken = RefreshToken.builder().token(token).build();

        when(refreshTokenRepository.findByToken(token)).thenReturn(Optional.of(refreshToken));

        Optional<RefreshToken> foundToken = refreshTokenService.findByToken(token);

        assertTrue(foundToken.isPresent());
        assertEquals(refreshToken, foundToken.get());
        verify(refreshTokenRepository).findByToken(token);
    }

    @Test
    void testVerifyExpiration() {
        RefreshToken refreshToken = RefreshToken.builder()
                .expiryDate(Instant.MAX)
                .build();

        RefreshToken result = refreshTokenService.verifyExpiration(refreshToken);

        assertEquals(refreshToken, result);
    }

    @Test
    void testVerifyExpirationExpiredToken() {
        RefreshToken refreshToken = RefreshToken.builder()
                .expiryDate(Instant.MIN)
                .build();

        assertThrows(TokenRefreshException.class, () -> refreshTokenService.verifyExpiration(refreshToken));
        verify(refreshTokenRepository).delete(refreshToken);
    }

    @Test
    void testRemoveByUser() {

        String username = "testuser";
        User user = User.builder().username(username).build();

        when(userService.getByUsername(username)).thenReturn(user);

        refreshTokenService.removeByUser(username);

        verify(userService).getByUsername(username);
        verify(refreshTokenRepository).deleteByUser(user);
    }
}
