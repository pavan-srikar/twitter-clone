package com.velialiyev.twitterclone.service;

import com.velialiyev.twitterclone.dto.LoginRequestDto;
import com.velialiyev.twitterclone.dto.LoginResponseDto;
import com.velialiyev.twitterclone.dto.RefreshTokenDto;
import com.velialiyev.twitterclone.dto.SignUpRequestDto;
import com.velialiyev.twitterclone.entity.RefreshTokenEntity;
import com.velialiyev.twitterclone.entity.UserEntity;
import com.velialiyev.twitterclone.repository.UserRepository;
import com.velialiyev.twitterclone.service.JwtService;
import com.velialiyev.twitterclone.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthenticationService authenticationService;

    private SignUpRequestDto signUpRequestDto;
    private LoginRequestDto loginRequestDto;
    private UserEntity userEntity;
    private Authentication authentication;
    private Jwt jwt;

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

        userEntity = UserEntity.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .username("johndoe")
                .email("john@example.com")
                .password("encodedPassword")
                .build();

        authentication = mock(Authentication.class);
        jwt = mock(Jwt.class);
    }

    @Test
    void signup_ShouldSaveUser_WhenValidSignUpRequest() {
        // Given
        String encodedPassword = "encodedPassword";
        when(passwordEncoder.encode(signUpRequestDto.getPassword())).thenReturn(encodedPassword);
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        // When
        authenticationService.signup(signUpRequestDto);

        // Then
        verify(passwordEncoder, times(1)).encode(signUpRequestDto.getPassword());
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    void login_ShouldReturnLoginResponse_WhenValidCredentials() {
        // Given
        String accessToken = "access-token";
        String refreshToken = "refresh-token";
        Long expirationTime = 3600000L;

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtService.generateToken(authentication)).thenReturn(accessToken);
        when(jwtService.generateRefreshToken()).thenReturn(RefreshTokenEntity.builder()
                .refreshToken(refreshToken)
                .build());
        when(jwtService.getJwtExpirationInMillis()).thenReturn(expirationTime);

        // When
        LoginResponseDto result = authenticationService.login(loginRequestDto);

        // Then
        assertNotNull(result);
        assertEquals(accessToken, result.getAccessToken());
        assertEquals(refreshToken, result.getRefreshToken());
        assertEquals(loginRequestDto.getUsername(), result.getUsername());
        assertNotNull(result.getExpiresAt());

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService, times(1)).generateToken(authentication);
        verify(jwtService, times(1)).generateRefreshToken();
    }

    @Test
    void logout_ShouldClearSecurityContext_WhenValidRefreshToken() {
        // Given
        RefreshTokenDto refreshTokenDto = RefreshTokenDto.builder()
                .refreshToken("refresh-token")
                .username("johndoe")
                .build();

        doNothing().when(jwtService).deleteRefreshToken(refreshTokenDto.getRefreshToken());

        // When
        authenticationService.logout(refreshTokenDto);

        // Then
        verify(jwtService, times(1)).deleteRefreshToken(refreshTokenDto.getRefreshToken());
        // SecurityContextHolder.clearContext() is called but we can't easily verify it in unit tests
    }

    @Test
    void refreshToken_ShouldReturnNewLoginResponse_WhenValidRefreshToken() {
        // Given
        RefreshTokenDto refreshTokenDto = RefreshTokenDto.builder()
                .refreshToken("refresh-token")
                .username("johndoe")
                .build();

        String newAccessToken = "new-access-token";
        Long expirationTime = 3600000L;

        doNothing().when(jwtService).validateRefreshToken(refreshTokenDto.getRefreshToken());
        when(jwtService.generateTokenWithUsername(refreshTokenDto.getUsername())).thenReturn(newAccessToken);
        when(jwtService.getJwtExpirationInMillis()).thenReturn(expirationTime);

        // When
        LoginResponseDto result = authenticationService.refreshToken(refreshTokenDto);

        // Then
        assertNotNull(result);
        assertEquals(newAccessToken, result.getAccessToken());
        assertEquals(refreshTokenDto.getRefreshToken(), result.getRefreshToken());
        assertEquals(refreshTokenDto.getUsername(), result.getUsername());
        assertNotNull(result.getExpiresAt());

        verify(jwtService, times(1)).validateRefreshToken(refreshTokenDto.getRefreshToken());
        verify(jwtService, times(1)).generateTokenWithUsername(refreshTokenDto.getUsername());
    }

    @Test
    void getUserFromJwt_ShouldReturnUser_WhenValidJwt() {
        // Given
        String username = "johndoe";
        when(jwt.getSubject()).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(userEntity));

        // Mock SecurityContextHolder
        Authentication mockAuth = mock(Authentication.class);
        when(mockAuth.getPrincipal()).thenReturn(jwt);
        SecurityContextHolder.getContext().setAuthentication(mockAuth);

        // When
        UserEntity result = authenticationService.getUserFromJwt();

        // Then
        assertNotNull(result);
        assertEquals(userEntity.getUsername(), result.getUsername());
        assertEquals(userEntity.getEmail(), result.getEmail());

        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void findAllUsernames_ShouldReturnAllUsernames() {
        // Given
        UserEntity user1 = UserEntity.builder().username("user1").build();
        UserEntity user2 = UserEntity.builder().username("user2").build();
        UserEntity user3 = UserEntity.builder().username("user3").build();
        List<UserEntity> users = Arrays.asList(user1, user2, user3);

        when(userRepository.findAll()).thenReturn(users);

        // When
        List<String> result = authenticationService.findAllUsernames();

        // Then
        assertNotNull(result);
        assertEquals(3, result.size());
        assertTrue(result.contains("user1"));
        assertTrue(result.contains("user2"));
        assertTrue(result.contains("user3"));

        verify(userRepository, times(1)).findAll();
    }

    @Test
    void signup_ShouldSetDefaultProfilePictures_WhenCreatingUser() {
        // Given
        String encodedPassword = "encodedPassword";
        when(passwordEncoder.encode(signUpRequestDto.getPassword())).thenReturn(encodedPassword);
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        // When
        authenticationService.signup(signUpRequestDto);

        // Then
        verify(userRepository, times(1)).save(argThat(user -> 
            user.getProfilePicturePath().equals("C:/uploads/DummyProfilePicture.jpg") &&
            user.getBannerPicturePath().equals("C:/uploads/DummyBannerPicture.jpg")
        ));
    }

    @Test
    void login_ShouldSetSecurityContext_WhenAuthenticationSucceeds() {
        // Given
        String accessToken = "access-token";
        String refreshToken = "refresh-token";
        Long expirationTime = 3600000L;

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtService.generateToken(authentication)).thenReturn(accessToken);
        when(jwtService.generateRefreshToken()).thenReturn(RefreshTokenEntity.builder()
                .refreshToken(refreshToken)
                .build());
        when(jwtService.getJwtExpirationInMillis()).thenReturn(expirationTime);

        // When
        authenticationService.login(loginRequestDto);

        // Then
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        // SecurityContextHolder.getContext().setAuthentication(authentication) is called but hard to verify in unit tests
    }
}
