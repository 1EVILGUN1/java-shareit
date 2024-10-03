package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.InternalServerErrorException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.ValidationException;
import java.util.Collection;
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
    public UserDto save(UserCreateDto user) {
        User newUser = UserMapper.mapUserCreatedDtoToUser(user);
        User savedUser = repository.save(newUser);
        return UserMapper.mapToUserDto(savedUser);
    }

    @Override
    @Transactional
    public UserDto update(long userId, UserUpdateDto userDto) {
        User userFromDb = checkUserDto(userId);
        User user = UserMapper.mapUserUpdatedDtoToUser(userDto, userId);
        userFromDb.setEmail(user.getEmail() != null ? user.getEmail() : userFromDb.getEmail());
        userFromDb.setName(user.getName() != null ? user.getName() : userFromDb.getName());
        User updatedUser = repository.save(userFromDb);
        return UserMapper.mapToUserDto(updatedUser);
    }

    @Override
    @Transactional
    public UserDto delete(Long id) {
        UserDto deletedUser = findById(id);
        repository.deleteById(id);
        return deletedUser;
    }

    @Override
    public UserDto findById(long id) {
        return UserMapper.mapToUserDto(repository.findById(id).orElseThrow(() ->
                new ValidationException(String.format("Пользователь с ID %d не найден", id))));
    }

    @Override
    public User checkUserDto(long id) {
        return repository.findById(id).orElseThrow(() ->
                new InternalServerErrorException(String.format("Пользователь с ID %d не найден", id)));
    }
}
