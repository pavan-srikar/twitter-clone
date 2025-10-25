package com.velialiyev.twitterclone.repository;

import com.velialiyev.twitterclone.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        userEntity = UserEntity.builder()
                .firstName("John")
                .lastName("Doe")
                .username("johndoe")
                .email("john@example.com")
                .password("encodedPassword")
                .profilePicturePath("profile.jpg")
                .bannerPicturePath("banner.jpg")
                .build();
    }

    @Test
    void save_ShouldPersistUser_WhenValidUserEntity() {
        // When
        UserEntity savedUser = userRepository.save(userEntity);

        // Then
        assertNotNull(savedUser.getId());
        assertEquals(userEntity.getUsername(), savedUser.getUsername());
        assertEquals(userEntity.getEmail(), savedUser.getEmail());
        assertEquals(userEntity.getFirstName(), savedUser.getFirstName());
        assertEquals(userEntity.getLastName(), savedUser.getLastName());
    }

    @Test
    void findByUsername_ShouldReturnUser_WhenUserExists() {
        // Given
        UserEntity savedUser = userRepository.save(userEntity);

        // When
        Optional<UserEntity> foundUser = userRepository.findByUsername(savedUser.getUsername());

        // Then
        assertTrue(foundUser.isPresent());
        assertEquals(savedUser.getId(), foundUser.get().getId());
        assertEquals(savedUser.getUsername(), foundUser.get().getUsername());
    }

    @Test
    void findByUsername_ShouldReturnEmpty_WhenUserDoesNotExist() {
        // When
        Optional<UserEntity> foundUser = userRepository.findByUsername("nonexistent");

        // Then
        assertFalse(foundUser.isPresent());
    }

    @Test
    void findByEmail_ShouldReturnUser_WhenUserExists() {
        // Given
        UserEntity savedUser = userRepository.save(userEntity);

        // When
        Optional<UserEntity> foundUser = userRepository.findByEmail(savedUser.getEmail());

        // Then
        assertTrue(foundUser.isPresent());
        assertEquals(savedUser.getId(), foundUser.get().getId());
        assertEquals(savedUser.getEmail(), foundUser.get().getEmail());
    }

    @Test
    void findByEmail_ShouldReturnEmpty_WhenUserDoesNotExist() {
        // When
        Optional<UserEntity> foundUser = userRepository.findByEmail("nonexistent@example.com");

        // Then
        assertFalse(foundUser.isPresent());
    }

    @Test
    void findAll_ShouldReturnAllUsers_WhenUsersExist() {
        // Given
        UserEntity user1 = UserEntity.builder()
                .firstName("John")
                .lastName("Doe")
                .username("johndoe")
                .email("john@example.com")
                .password("encodedPassword")
                .build();

        UserEntity user2 = UserEntity.builder()
                .firstName("Jane")
                .lastName("Smith")
                .username("janesmith")
                .email("jane@example.com")
                .password("encodedPassword")
                .build();

        userRepository.save(user1);
        userRepository.save(user2);

        // When
        List<UserEntity> allUsers = userRepository.findAll();

        // Then
        assertTrue(allUsers.size() >= 2);
        assertTrue(allUsers.stream().anyMatch(user -> user.getUsername().equals("johndoe")));
        assertTrue(allUsers.stream().anyMatch(user -> user.getUsername().equals("janesmith")));
    }

    @Test
    void existsByUsername_ShouldReturnTrue_WhenUsernameExists() {
        // Given
        UserEntity savedUser = userRepository.save(userEntity);

        // When
        boolean exists = userRepository.findByUsername(savedUser.getUsername()).isPresent();

        // Then
        assertTrue(exists);
    }

    @Test
    void existsByUsername_ShouldReturnFalse_WhenUsernameDoesNotExist() {
        // When
        boolean exists = userRepository.findByUsername("nonexistent").isPresent();

        // Then
        assertFalse(exists);
    }

    @Test
    void existsByEmail_ShouldReturnTrue_WhenEmailExists() {
        // Given
        UserEntity savedUser = userRepository.save(userEntity);

        // When
        boolean exists = userRepository.findByEmail(savedUser.getEmail()).isPresent();

        // Then
        assertTrue(exists);
    }

    @Test
    void existsByEmail_ShouldReturnFalse_WhenEmailDoesNotExist() {
        // When
        boolean exists = userRepository.findByEmail("nonexistent@example.com").isPresent();

        // Then
        assertFalse(exists);
    }

    @Test
    void deleteById_ShouldRemoveUser_WhenUserExists() {
        // Given
        UserEntity savedUser = userRepository.save(userEntity);
        Long userId = savedUser.getId();

        // When
        userRepository.deleteById(userId);

        // Then
        Optional<UserEntity> deletedUser = userRepository.findById(userId);
        assertFalse(deletedUser.isPresent());
    }

    @Test
    void findByUsername_ShouldBeCaseSensitive() {
        // Given
        UserEntity savedUser = userRepository.save(userEntity);

        // When
        Optional<UserEntity> foundUserLower = userRepository.findByUsername("johndoe");
        Optional<UserEntity> foundUserUpper = userRepository.findByUsername("JOHNDOE");

        // Then
        assertTrue(foundUserLower.isPresent());
        assertFalse(foundUserUpper.isPresent());
    }
}
