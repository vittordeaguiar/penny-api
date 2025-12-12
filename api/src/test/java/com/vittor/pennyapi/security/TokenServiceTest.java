package com.vittor.pennyapi.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.vittor.pennyapi.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    @InjectMocks
    private TokenService tokenService;

    private User user;
    private final String testSecret = "test-secret-key-for-jwt-tokens-must-be-long-enough";
    private final Long testExpiration = 3600000L; // 1 hour in milliseconds

    @BeforeEach
    void setUp() {
        // Inject test values for @Value fields using ReflectionTestUtils
        ReflectionTestUtils.setField(tokenService, "secret", testSecret);
        ReflectionTestUtils.setField(tokenService, "expiration", testExpiration);

        user = new User();
        user.setId(UUID.randomUUID());
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");
    }

    @Test
    @DisplayName("Should generate token successfully")
    void generateToken_Success() {
        // When
        String token = tokenService.generateToken(user);

        // Then
        assertNotNull(token);
        assertFalse(token.isEmpty());

        // Verify token structure (JWT has 3 parts separated by dots)
        String[] tokenParts = token.split("\\.");
        assertEquals(3, tokenParts.length);

        // Decode and verify claims
        DecodedJWT decodedJWT = JWT.decode(token);
        assertEquals("penny-api", decodedJWT.getIssuer());
        assertEquals(user.getEmail(), decodedJWT.getSubject());
        assertEquals(user.getId().toString(), decodedJWT.getClaim("userId").asString());
        assertNotNull(decodedJWT.getExpiresAt());
    }

    @Test
    @DisplayName("Should validate valid token successfully")
    void validateToken_ValidToken_ReturnsEmail() {
        // Given
        String token = tokenService.generateToken(user);

        // When
        String email = tokenService.validateToken(token);

        // Then
        assertNotNull(email);
        assertEquals(user.getEmail(), email);
    }

    @Test
    @DisplayName("Should return empty string for invalid token")
    void validateToken_InvalidToken_ReturnsEmptyString() {
        // Given
        String invalidToken = "invalid.token.here";

        // When
        String result = tokenService.validateToken(invalidToken);

        // Then
        assertEquals("", result);
    }

    @Test
    @DisplayName("Should return empty string for expired token")
    void validateToken_ExpiredToken_ReturnsEmptyString() {
        // Given - Create token with expiration in the past
        ReflectionTestUtils.setField(tokenService, "expiration", -1000L);
        String expiredToken = tokenService.generateToken(user);

        // Reset expiration to normal
        ReflectionTestUtils.setField(tokenService, "expiration", testExpiration);

        // When
        String result = tokenService.validateToken(expiredToken);

        // Then
        assertEquals("", result);
    }

    @Test
    @DisplayName("Should return empty string for token with wrong issuer")
    void validateToken_WrongIssuer_ReturnsEmptyString() {
        // Given - Create token with different issuer
        Algorithm algorithm = Algorithm.HMAC256(testSecret);
        String tokenWithWrongIssuer = JWT.create()
                .withIssuer("wrong-issuer")
                .withSubject(user.getEmail())
                .withClaim("userId", user.getId().toString())
                .withExpiresAt(Instant.now().plusSeconds(3600))
                .sign(algorithm);

        // When
        String result = tokenService.validateToken(tokenWithWrongIssuer);

        // Then
        assertEquals("", result);
    }

    @Test
    @DisplayName("Should throw RuntimeException when JWT creation fails")
    void generateToken_JWTCreationFails_ThrowsRuntimeException() {
        // Given - Set invalid secret to force JWT creation to fail
        ReflectionTestUtils.setField(tokenService, "secret", "");

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            tokenService.generateToken(user);
        });
    }

    @Test
    @DisplayName("Should return empty string for null token")
    void validateToken_NullToken_ReturnsEmptyString() {
        // When
        String result = tokenService.validateToken(null);

        // Then
        assertEquals("", result);
    }

    @Test
    @DisplayName("Should return empty string for empty token")
    void validateToken_EmptyToken_ReturnsEmptyString() {
        // When
        String result = tokenService.validateToken("");

        // Then
        assertEquals("", result);
    }
}
