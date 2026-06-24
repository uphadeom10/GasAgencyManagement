package com.utility.agency.security;

import com.agency.management.security.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtUtilsTest {

    @InjectMocks
    private JwtUtils jwtUtils;

    private static final String SECRET = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
    private static final long EXPIRATION = 86400000;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtUtils, "secret", SECRET);
        ReflectionTestUtils.setField(jwtUtils, "expiration", EXPIRATION);
    }

    @Test
    void testValidateJwtToken_ValidToken() {
        String token = jwtUtils.generateToken("testUser", 1L);
        assertTrue(jwtUtils.validateJwtToken(token));
    }

    @Test
    void testValidateJwtToken_InvalidToken() {
        assertFalse(jwtUtils.validateJwtToken("invalid.token.here"));
    }

    @Test
    void testValidateJwtToken_ExpiredToken() {
        // Generate token with very short expiration
        ReflectionTestUtils.setField(jwtUtils, "expiration", 1L);
        String token = jwtUtils.generateToken("testUser", 1L);

        // Wait for token to expire
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        assertFalse(jwtUtils.validateJwtToken(token));
    }

    @Test
    void testGetUsernameFromToken_ValidToken() {
        String username = "testUser";
        String token = jwtUtils.generateToken(username, 1L);
        assertEquals(username, jwtUtils.getUsernameFromToken(token));
    }

    @Test
    void testGetUsernameFromToken_InvalidToken() {
        assertNull(jwtUtils.getUsernameFromToken("invalid.token.here"));
    }

    @Test
    void testGenerateToken_ContainsCorrectClaims() {
        String username = "testUser";
        Long userId = 1L;
        String token = jwtUtils.generateToken(username, userId);

        assertNotNull(token);
        assertTrue(jwtUtils.validateJwtToken(token));
        assertEquals(username, jwtUtils.getUsernameFromToken(token));
    }
}
