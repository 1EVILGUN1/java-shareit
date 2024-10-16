package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.EmailDoubleException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.validators.UserValidator;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserServiceImpl userService;

    @Captor
    ArgumentCaptor<User> userArgumentCaptor;

    @Test
    void findAllUsers_whenInvoked_thenListOfUsersReturned() {
        User expectedUser = new User(1L, "name", "email@mail.ru");
        when(userRepository.findAll()).thenReturn(List.of(expectedUser));

        List<User> actualUsersList = userService.findAllUsers();

        assertEquals(expectedUser, actualUsersList.getFirst());
    }

    @Test
    void addUser_whenEmailNotDouble_thenUserReturned() {
        User userToSave = new User(1L, "name", "email@mail.ru");
        when(userRepository.save(userToSave)).thenReturn(userToSave);

        User actualUser = userService.save(userToSave);

        assertEquals(userToSave, actualUser);
        verify(userRepository, times(1)).save(userToSave);
    }

    @Test
    void addUser_whenEmailDouble_thenEmailDoubleExceptionThrownAndUserNotSaved() {
        User userToSave = new User(1L, "name", "email@mail.ru");
        User userInDb = new User(2L, "name2", "email@mail.ru");

        try (MockedStatic<UserValidator> userValidator = mockStatic(UserValidator.class)) {
            when(userRepository.findUserByEmail(userToSave.getEmail())).thenReturn(Optional.of(userInDb));

            userValidator.when(() -> UserValidator.checkEmailIsUnique(Optional.of(userInDb)))
                    .thenThrow(new EmailDoubleException("Email должен быть уникальным"));

            EmailDoubleException exception = assertThrows(EmailDoubleException.class, () -> {
                userService.save(userToSave);
            });

            assertEquals("Email должен быть уникальным", exception.getMessage());
            verify(userRepository, never()).save(userToSave);
        }
    }

    @Test
    void updateUser_whenUserIsFound_thenUpdatedUserReturned() {
        User oldUser = new User(1L, "name", "email@mail.ru");
        User newUser = new User(300L, "newName", "newEmail@mail.ru");
        when(userRepository.findById(1L)).thenReturn(Optional.of(oldUser));

        userService.update(1L, newUser);

        verify(userRepository, times(1)).save(userArgumentCaptor.capture());
        User savedUser = userArgumentCaptor.getValue();

        assertEquals(1L, savedUser.getId());
        assertEquals("newName", savedUser.getName());
        assertEquals("newEmail@mail.ru", savedUser.getEmail());
    }

    @Test
    void updateUser_whenUserIdUserNotFound_thenNotFoundExceptionThrown() {
        User userToUpdate = new User(1L, "name", "email@mail.ru");
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            userService.update(1L, userToUpdate);
        });
        verify(userRepository, never()).save(userToUpdate);
    }

    @Test
    void updateUser_whenUserIsFoundAndEmailNotIsUnique_thenEmailDoubleExceptionAndUserNotSaved() {

        User userToUpdate = new User(1L, "name", "doubleEmail@mail.ru");
        User oldUser = new User(1L, "name", "oldEmail@mail.ru");
        User otherUserInDb = new User(1L, "otherName", "doubleEmail@mail.ru");
        when(userRepository.findById(1L)).thenReturn(Optional.of(oldUser));

        try (MockedStatic<UserValidator> userValidator = mockStatic(UserValidator.class)) {
            when(userRepository.findUserByEmail(userToUpdate.getEmail())).thenReturn(Optional.of(otherUserInDb));

            userValidator.when(() -> UserValidator.checkEmailIsUnique(Optional.of(otherUserInDb)))
                    .thenThrow(new EmailDoubleException("Email должен быть уникальным"));

            EmailDoubleException exception = assertThrows(EmailDoubleException.class, () -> {
                userService.update(1L, userToUpdate);
            });

            assertEquals("Email должен быть уникальным", exception.getMessage());
            verify(userRepository, never()).save(userToUpdate);
        }
    }


    @Test
    void findUserById_whenUserFound_thenUserReturned() {
        Long userId = 1L;
        User expectedUser = new User();
        when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));

        User actualUser = userService.findById(userId);

        assertEquals(expectedUser, actualUser);
    }

    @Test
    void findUserById_whenUserNotFound_thenNotFoundExceptionThrown() {
        long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.findById(userId));
    }

    @Test
    void deleteUser_whenUserExists_thenDeleteIsCalled() {
        Long userId = 1L;

        userService.delete(userId);

        verify(userRepository, times(1)).deleteById(userId);
    }
}
