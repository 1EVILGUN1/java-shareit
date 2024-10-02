package ru.practicum.shareit.user.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public Collection<UserDto> getAll() {
        return repository.findAll().stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDto save(UserDto userDto) {
        User user = UserMapper.mapToUser(userDto);
        return UserMapper.mapToUserDto(repository.save(user));
    }

    @Override
    @Transactional
    public UserDto update(long userId, UserDto userDto) {
        User userFromDb = checkUserDtoById(userId);
        User user = UserMapper.mapToUser(userDto);
        userFromDb.setEmail(user.getEmail() != null ? user.getEmail() : userFromDb.getEmail());
        userFromDb.setName(user.getName() != null ? user.getName() : userFromDb.getName());
        User updateUser = repository.save(userFromDb);
        return UserMapper.mapToUserDto(updateUser);
    }

    @Override
    @Transactional
    public UserDto delete(Long id) {
        User user = checkUserDtoById(id);
        repository.deleteById(id);
        return UserMapper.mapToUserDto(user);
    }

    @Override
    public UserDto findById(long id) {
        User user = checkUserDtoById(id);
        return UserMapper.mapToUserDto(user);
    }

    private User checkUserDtoById(long id) {
        return repository.findById(id).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь с ID %d не найден", id)));
    }
}
