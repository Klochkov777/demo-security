package dev.klochkov.demo_security.service;

import dev.klochkov.demo_security.dto.security.JwtAuthenticationResponse;
import dev.klochkov.demo_security.dto.security.SignInDto;
import dev.klochkov.demo_security.dto.security.SignUpDto;
import dev.klochkov.demo_security.entity.User;
import enumeration.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {
    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private RefreshTokenService refreshTokenService;

    @InjectMocks
    private AuthenticationService authenticationService;
    @Mock
    private UserDetailsService userDetailsService;

    @Test
    void testSignUp() {
        SignUpDto signUpDto = new SignUpDto();
        signUpDto.setEmail("test@example.com");
        signUpDto.setUsername("testuser");
        signUpDto.setPassword("password");

        User user = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("encodedPassword")
                .role(Role.USER)
                .build();

        when(passwordEncoder.encode(signUpDto.getPassword())).thenReturn("encodedPassword");
        when(userService.create(any(User.class))).thenReturn(user);
        when(jwtService.generateToken(any(User.class))).thenReturn("accessToken");
        when(jwtService.generateRefreshToken(any(User.class))).thenReturn("refreshToken");

        JwtAuthenticationResponse response = authenticationService.signUp(signUpDto);

        assertEquals("accessToken", response.getAccessToken());
        assertEquals("refreshToken", response.getRefreshToken());

        verify(userService).create(any(User.class));
        verify(jwtService).generateToken(any(User.class));
        verify(jwtService).generateRefreshToken(any(User.class));

    }

    @Test
    void testSignIn() {
        SignInDto signInDto = new SignInDto();
        signInDto.setUsername("testuser");
        signInDto.setPassword("password");

        User user = User.builder().username("testuser").build();

        when(userService.userDetailsService()).thenReturn(userDetailsService);
        when(userDetailsService.loadUserByUsername(any(String.class))).thenReturn(user);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(jwtService.generateToken(user)).thenReturn("accessToken");
        when(jwtService.generateRefreshToken(user)).thenReturn("refreshToken");

        JwtAuthenticationResponse response = authenticationService.signIn(signInDto);

        assertEquals("accessToken", response.getAccessToken());
        assertEquals("refreshToken", response.getRefreshToken());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService).generateToken(user);
        verify(jwtService).generateRefreshToken(user);
    }
}
