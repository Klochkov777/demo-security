package dev.klochkov.demo_security.controller;

import dev.klochkov.demo_security.dto.security.JwtAuthenticationResponse;
import dev.klochkov.demo_security.dto.security.SignInDto;
import dev.klochkov.demo_security.dto.security.SignUpDto;
import dev.klochkov.demo_security.dto.security.TokenRefreshDto;
import dev.klochkov.demo_security.entity.RefreshToken;
import dev.klochkov.demo_security.entity.User;
import dev.klochkov.demo_security.service.AuthenticationService;
import dev.klochkov.demo_security.service.JwtService;
import dev.klochkov.demo_security.service.RefreshTokenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Optional;

import static org.mockito.Mockito.*;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerIT {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationService authenticationService;
    @MockBean
    private JwtService jwtService;

    @MockBean
    private RefreshTokenService refreshTokenService;

    @Test
    void testSignUp() throws Exception {
        SignUpDto signUpDto = new SignUpDto();
        signUpDto.setUsername("testuser");
        signUpDto.setEmail("test@example.com");
        signUpDto.setPassword("password");

        JwtAuthenticationResponse jwtResponse = new JwtAuthenticationResponse("accessToken", "refreshToken");

        when(authenticationService.signUp(any(SignUpDto.class))).thenReturn(jwtResponse);

        mockMvc.perform(post("/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {
                                        "username": "testuser",
                                        "email": "test@example.com",
                                        "password": "password"
                                    }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("accessToken"))
                .andExpect(jsonPath("$.refreshToken").value("refreshToken"));

        verify(authenticationService, times(1)).signUp(any(SignUpDto.class));
    }

    @Test
    void testSignIn() throws Exception {
        SignInDto signInDto = new SignInDto();
        signInDto.setUsername("testuser");
        signInDto.setPassword("password");

        JwtAuthenticationResponse jwtResponse = new JwtAuthenticationResponse("accessToken", "refreshToken");

        when(authenticationService.signIn(any(SignInDto.class))).thenReturn(jwtResponse);

        mockMvc.perform(post("/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {
                                        "username": "testuser",
                                        "password": "password"
                                    }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("accessToken"))
                .andExpect(jsonPath("$.refreshToken").value("refreshToken"));

        verify(authenticationService, times(1)).signIn(any(SignInDto.class));
    }

    @Test
    void testLogout() throws Exception {
        String refreshToken = "validRefreshToken";
        String username = "testuser";

        TokenRefreshDto requestDto = new TokenRefreshDto();
        requestDto.setRefreshToken(refreshToken);

        when(jwtService.extractUserName(refreshToken)).thenReturn(username);

        mockMvc.perform(post("/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "refreshToken": "validRefreshToken"
                                }
                                """))
                .andExpect(status().isOk());

        verify(jwtService, times(1)).extractUserName(refreshToken);
        verify(refreshTokenService, times(1)).removeByUser(username);
    }

    @Test
    void testRefreshToken() throws Exception {
        String refreshToken = "validRefreshToken";
        TokenRefreshDto requestDto = new TokenRefreshDto();
        requestDto.setRefreshToken(refreshToken);
        JwtAuthenticationResponse jwtResponse = new JwtAuthenticationResponse("newAccessToken", refreshToken);

        User user = User.builder().username("testuser").build();

        when(refreshTokenService.findByToken(refreshToken))
                .thenReturn(Optional.of(new RefreshToken(1l, refreshToken, user, Instant.now().plusSeconds(3600))));
        when(refreshTokenService.verifyExpiration(any(RefreshToken.class)))
                .thenReturn(new RefreshToken(1l, refreshToken, user, Instant.now().plusSeconds(3600)));
        when(jwtService.generateToken(user)).thenReturn("newAccessToken");

        mockMvc.perform(post("/auth/refresh-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "refreshToken": "validRefreshToken"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("newAccessToken"))
                .andExpect(jsonPath("$.refreshToken").value(refreshToken));

        verify(refreshTokenService, times(1)).findByToken(refreshToken);
        verify(refreshTokenService, times(1)).verifyExpiration(any(RefreshToken.class));
        verify(jwtService, times(1)).generateToken(user);
    }
}
