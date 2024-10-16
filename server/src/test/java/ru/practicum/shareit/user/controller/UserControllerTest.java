package ru.practicum.shareit.user.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    void getAllUsers_whenInvoked_thenUserDtosCollectionReturned() {
        List<User> expectedUsers = List.of(new User(1L, "UserName", "user@mail.ru"));
        List<UserDto> expectedUserDtos = expectedUsers
                .stream()
                .map(UserMapper::mapToDto)
                .toList();
        when(userService.findAllUsers()).thenReturn(expectedUsers);

        List<UserDto> actualUserDtos = userController.getAllUsers();

        assertEquals(expectedUserDtos, actualUserDtos);
    }

    @Test
    void saveNewUser_whenInvoked_thenUserReturned() {
        User userToSave = new User(1L, "UserName", "user@mail.ru");
        when(userService.save(userToSave)).thenReturn(userToSave);

        User actualUser = userController.save(userToSave);

        assertEquals(userToSave, actualUser);
    }

    @Test
    void findById_whenInvoked_thenUserDtoReturned() {
        User expectedUser = new User(1L, "UserName", "user@mail.ru");
        UserDto expectedUserDto = UserMapper.mapToDto(expectedUser);
        when(userService.findById(anyLong())).thenReturn(expectedUser);

        UserDto actualUserDto = userController.findById(1L);

        assertEquals(expectedUserDto, actualUserDto);
    }

    @Test
    void update_whenInvoked_thenUpdatedUserReturned() {
        User userToUpdate = new User(1L, "UserName", "user@mail.ru");
        when(userService.update(1L, userToUpdate)).thenReturn(userToUpdate);

        User updatedUser = userController.update(1L, userToUpdate);

        assertEquals(userToUpdate, updatedUser);
    }
}
