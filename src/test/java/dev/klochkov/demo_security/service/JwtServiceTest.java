package dev.klochkov.demo_security.service;

import dev.klochkov.demo_security.entity.User;
import enumeration.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ContextConfiguration(classes = {JwtService.class})
public class JwtServiceTest {

    @Autowired
    private JwtService jwtService;

    @Test
    void testGenerateToken() {
        User user = new User();
        user.setId(UUID.fromString("f3cda4cd-4be9-467f-ba46-155a4553a61d"));
        user.setUsername("testuser");
        user.setPassword("password");
        user.setRole(Role.USER);

        String token = jwtService.generateToken(user);

        assertNotNull(token);
        assertTrue(token.length() > 0);
    }

    @Test
    void testExtractUserName() {
        User user = new User();
        user.setId(UUID.fromString("f3cda4cd-4be9-467f-ba46-155a4553a61d"));
        user.setUsername("testuser");
        user.setPassword("password");
        user.setRole(Role.USER);

        String token = jwtService.generateToken(user);


        String username = jwtService.extractUserName(token);


        assertEquals("testuser", username);
    }

    @Test
    void testIsTokenValid() {
        User user = new User();
        user.setId(UUID.fromString("f3cda4cd-4be9-467f-ba46-155a4553a61d"));
        user.setUsername("testuser");
        user.setPassword("password");
        user.setRole(Role.USER);
        String token = jwtService.generateToken(user);

        boolean isValid = jwtService.isTokenValid(token, user);

        assertTrue(isValid);
    }
}
