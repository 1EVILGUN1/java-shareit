package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.controller.mapper.model.UserDto;

import java.util.Collection;

public interface UserService {
    Collection<UserDto> getAll();

    UserDto save(UserDto user);

    UserDto update(long userId, UserDto user);

    UserDto delete(Long id);

    UserDto findById(long id);
}
