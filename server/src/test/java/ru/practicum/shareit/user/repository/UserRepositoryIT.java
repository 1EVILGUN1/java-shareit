package ru.practicum.shareit.user.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class UserRepositoryIT {

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    public void setUp() {
        testUser = new User();
        testUser.setName("Test User");
        testUser.setEmail("testuser@example.com");
        userRepository.save(testUser);
    }

    @Test
    public void whenFindUserByEmail_thenReturnUser() {
        Optional<User> foundUser = userRepository.findUserByEmail("testuser@example.com");
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getName()).isEqualTo(testUser.getName());
    }

    @Test
    public void whenFindUserByEmailWithDifferentCase_thenReturnUser() {
        Optional<User> foundUser = userRepository.findUserByEmail("TESTUSER@EXAMPLE.COM");
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getName()).isEqualTo(testUser.getName());
    }

    @Test
    public void whenFindUserByEmail_notExistingEmail_thenReturnEmpty() {
        Optional<User> foundUser = userRepository.findUserByEmail("nonexistent@example.com");
        assertThat(foundUser).isNotPresent();
    }
}
