package ru.practicum.shareit.user.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdatedDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public Collection<UserDto> getUsers() {
        return repository.findAll().stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDto create(UserCreateDto user) {
        User newUser = UserMapper.mapUserCreatedDtoToUser(user);
        return UserMapper.mapToUserDto(repository.save(newUser));
    }

    @Override
    @Transactional
    public UserDto update(long userId, UserUpdatedDto userDto) {
        User userFromDb = checkUserDto(userId);
        User user = UserMapper.mapUserUpdatedDtoToUser(userDto, userId);
        userFromDb.setEmail(user.getEmail() != null ? user.getEmail() : userFromDb.getEmail());
        userFromDb.setName(user.getName() != null ? user.getName() : userFromDb.getName());
        repository.save(userFromDb);
        return UserMapper.mapToUserDto(userFromDb);
    }

    @Override
    @Transactional
    public UserDto remove(Long id) {
        UserDto userDto = getUserById(id);
        repository.deleteById(id);
        return userDto;
    }

    @Override
    public UserDto getUserById(long id) {
        return UserMapper.mapToUserDto(checkUserDto(id));
    }

    @Override
    public User checkUserDto(long userId) {
        return repository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь с ID %d не найден", userId)));
    }
}
