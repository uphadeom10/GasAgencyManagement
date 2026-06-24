package com.utility.agency.login;
import com.agency.management.common.ApiResponse;
import com.agency.management.login.dto.request.LoginRequestDto;
import com.agency.management.login.dto.response.LoginUserResponseDto;
import com.agency.management.login.service.impl.LoginServiceImpl;
import com.agency.management.masters.entity.Users;
import com.agency.management.masters.repository.UserRepository;
import com.agency.management.security.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LoginServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private LoginServiceImpl loginService;

    private LoginRequestDto validRequest;
    private LoginRequestDto invalidRequest;
    private Users mockUser;


    @BeforeEach
    void setUp() {
        validRequest = new LoginRequestDto();
        validRequest.setUsername("testuser");
        validRequest.setPassword("password");

        invalidRequest = new LoginRequestDto();
        invalidRequest.setUsername(null);
        invalidRequest.setPassword(null);

        mockUser = new Users();
        mockUser.setId(1L);
        mockUser.setUserName("testuser");
        mockUser.setPassword("encodedPassword");
        mockUser.setFirstName("Test");
        mockUser.setLastName("User");
        mockUser.setIsActive(true);
    }

    @Test
    void login_success() {
        // Arrange
        when(userRepository.findByUserName("testuser")).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(true);
        when(jwtUtils.generateToken("testuser", 1L)).thenReturn("mockToken");
        when(userRepository.getUserTypeByUsername("testuser")).thenReturn("ROLE_USER");

        // Act
        ResponseEntity<?> response = loginService.login(validRequest);
        ApiResponse<?> apiResponse = (ApiResponse<?>) response.getBody();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(apiResponse);
        assertEquals(HttpStatus.OK.value(), apiResponse.getStatusCode());
        assertEquals("Login successfully", apiResponse.getMessage());

        LoginUserResponseDto responseDto = (LoginUserResponseDto) apiResponse.getResult();
        assertEquals("Test User", responseDto.getUserName());
        assertEquals(1L, responseDto.getUserId());
        assertEquals("mockToken", responseDto.getToken());
        assertEquals("ROLE_USER", responseDto.getRole());
    }

    @Test
    void login_WithValidCredentials_ReturnsSuccessResponse() {
        // Arrange
        when(userRepository.findByUserName("testuser")).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(true);
        when(jwtUtils.generateToken("testuser", 1L)).thenReturn("mockToken");
        when(userRepository.getUserTypeByUsername("testuser")).thenReturn("ROLE_USER");

        // Act
        ResponseEntity<?> response = loginService.login(validRequest);
        ApiResponse<?> apiResponse = (ApiResponse<?>) response.getBody();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(apiResponse);
        assertEquals(HttpStatus.OK.value(), apiResponse.getStatusCode());
        assertEquals("Login successfully", apiResponse.getMessage());

        LoginUserResponseDto responseDto = (LoginUserResponseDto) apiResponse.getResult();
        assertEquals("Test User", responseDto.getUserName());
        assertEquals(1L, responseDto.getUserId());
        assertEquals("mockToken", responseDto.getToken());
        assertEquals("ROLE_USER", responseDto.getRole());
    }

    @Test
    void login_WithInvalidUsername() {
        when(userRepository.findByUserName("testuser")).thenReturn(Optional.empty());

        ResponseEntity<?> response = loginService.login(validRequest);
        ApiResponse<?> apiResponse = (ApiResponse<?>) response.getBody();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(apiResponse);
        assertEquals(HttpStatus.UNAUTHORIZED.value(), apiResponse.getStatusCode());
        assertEquals("Invalid Credentials", apiResponse.getMessage());
        assertNull(apiResponse.getCount());
    }

    @Test
    void login_WithInvalidPassword_ReturnsUnauthorized() {
        // Arrange
        when(userRepository.findByUserName("testuser")).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(false);

        // Act
        ResponseEntity<?> response = loginService.login(validRequest);
        ApiResponse<?> apiResponse = (ApiResponse<?>) response.getBody();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(apiResponse);
        assertEquals(HttpStatus.UNAUTHORIZED.value(), apiResponse.getStatusCode());
        assertEquals("Invalid Credentials", apiResponse.getMessage());
        assertNull(apiResponse.getResult());
    }
}
