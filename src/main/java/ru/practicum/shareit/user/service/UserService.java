package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserService {
    Collection<UserDto> getUsers();

    UserDto save(UserCreateDto user);

    UserDto update(long userId, UserUpdateDto userUpdateDto);

    UserDto delete(Long id);

    UserDto findById(long id);

    User checkUserDto(long id);
}
