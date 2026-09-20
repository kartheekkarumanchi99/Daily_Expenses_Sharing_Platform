package com.expensesharing.com.expensesharing.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class JwtServiceTest {

    private JwtService jwtService;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(
                "unit-test-secret-key-which-is-long-enough-0123456789",
                86_400_000L);
        userDetails = new User("alice", "pw", Collections.emptyList());
    }

    @Test
    void generatedTokenContainsUsername() {
        String token = jwtService.generateToken(userDetails);
        assertNotNull(token);
        assertEquals("alice", jwtService.extractUsername(token));
    }

    @Test
    void validTokenIsAcceptedForSameUser() {
        String token = jwtService.generateToken(userDetails);
        assertTrue(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    void tokenIsRejectedForDifferentUser() {
        String token = jwtService.generateToken(userDetails);
        UserDetails other = new User("bob", "pw", Collections.emptyList());
        assertFalse(jwtService.isTokenValid(token, other));
    }

    @Test
    void expiredTokenIsInvalid() {
        JwtService shortLived = new JwtService(
                "unit-test-secret-key-which-is-long-enough-0123456789",
                -1_000L);
        String token = shortLived.generateToken(userDetails);
        assertThrows(Exception.class, () -> shortLived.isTokenValid(token, userDetails));
    }
}
