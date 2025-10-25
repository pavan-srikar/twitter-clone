package com.velialiyev.twitterclone.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.velialiyev.twitterclone.dto.LoginRequestDto;
import com.velialiyev.twitterclone.dto.LoginResponseDto;
import com.velialiyev.twitterclone.dto.RefreshTokenDto;
import com.velialiyev.twitterclone.dto.SignUpRequestDto;
import com.velialiyev.twitterclone.service.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AuthenticationController.class)
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationService authenticationService;

    @Autowired
    private ObjectMapper objectMapper;

    private SignUpRequestDto signUpRequestDto;
    private LoginRequestDto loginRequestDto;
    private LoginResponseDto loginResponseDto;
    private RefreshTokenDto refreshTokenDto;

    @BeforeEach
    void setUp() {
        signUpRequestDto = SignUpRequestDto.builder()
                .firstName("John")
                .lastName("Doe")
                .username("johndoe")
                .email("john@example.com")
                .password("password123")
                .build();

        loginRequestDto = LoginRequestDto.builder()
                .username("johndoe")
                .password("password123")
                .build();

        loginResponseDto = LoginResponseDto.builder()
                .accessToken("access-token")
                .refreshToken("refresh-token")
                .username("johndoe")
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();

        refreshTokenDto = RefreshTokenDto.builder()
                .refreshToken("refresh-token")
                .username("johndoe")
                .build();
    }

    @Test
    void signUp_ShouldReturnOk_WhenValidSignUpRequest() throws Exception {
        // Given
        doNothing().when(authenticationService).signup(any(SignUpRequestDto.class));

        // When & Then
        mockMvc.perform(post("/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpRequestDto)))
                .andExpect(status().isOk());

        verify(authenticationService, times(1)).signup(any(SignUpRequestDto.class));
    }

    @Test
    void signUp_ShouldReturnBadRequest_WhenInvalidSignUpRequest() throws Exception {
        // Given
        SignUpRequestDto invalidSignUp = SignUpRequestDto.builder()
                .firstName("")
                .lastName("")
                .username("")
                .email("invalid-email")
                .password("")
                .build();

        // When & Then
        mockMvc.perform(post("/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidSignUp)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_ShouldReturnLoginResponse_WhenValidCredentials() throws Exception {
        // Given
        when(authenticationService.login(any(LoginRequestDto.class))).thenReturn(loginResponseDto);

        // When & Then
        mockMvc.perform(post("/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accessToken").value("access-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"))
                .andExpect(jsonPath("$.username").value("johndoe"));

        verify(authenticationService, times(1)).login(any(LoginRequestDto.class));
    }

    @Test
    void login_ShouldReturnBadRequest_WhenInvalidCredentials() throws Exception {
        // Given
        LoginRequestDto invalidLogin = LoginRequestDto.builder()
                .username("")
                .password("")
                .build();

        // When & Then
        mockMvc.perform(post("/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidLogin)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void logout_ShouldReturnOk_WhenValidRefreshToken() throws Exception {
        // Given
        doNothing().when(authenticationService).logout(any(RefreshTokenDto.class));

        // When & Then
        mockMvc.perform(post("/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshTokenDto)))
                .andExpect(status().isOk());

        verify(authenticationService, times(1)).logout(any(RefreshTokenDto.class));
    }

    @Test
    void refreshToken_ShouldReturnLoginResponse_WhenValidRefreshToken() throws Exception {
        // Given
        when(authenticationService.refreshToken(any(RefreshTokenDto.class))).thenReturn(loginResponseDto);

        // When & Then
        mockMvc.perform(post("/auth/refresh-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshTokenDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accessToken").value("access-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"))
                .andExpect(jsonPath("$.username").value("johndoe"));

        verify(authenticationService, times(1)).refreshToken(any(RefreshTokenDto.class));
    }

    @Test
    void findAllUsernames_ShouldReturnUsernamesList() throws Exception {
        // Given
        List<String> usernames = Arrays.asList("johndoe", "janedoe", "bobsmith");
        when(authenticationService.findAllUsernames()).thenReturn(usernames);

        // When & Then
        mockMvc.perform(get("/auth/usernames"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0]").value("johndoe"))
                .andExpect(jsonPath("$[1]").value("janedoe"))
                .andExpect(jsonPath("$[2]").value("bobsmith"));

        verify(authenticationService, times(1)).findAllUsernames();
    }
}
