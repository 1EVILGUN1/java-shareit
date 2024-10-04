package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdatedDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserService {
    Collection<UserDto> getUsers();

    UserDto create(UserCreateDto user);

    UserDto update(long userId, UserUpdatedDto userUpdatedDto);

    UserDto remove(Long id);

    UserDto getUserById(long id);

    User checkUserDto(long id);
}
