package ru.practicum.shareit.user.service.impl;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.user.controller.mapper.UserMapper;
import ru.practicum.shareit.user.controller.mapper.model.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public Collection<UserDto> getAll() {
        return UserMapper.mapToUserDtoList(userRepository.getAll());
    }

    @Override
    public UserDto save(UserDto userDto) {
        if (userDto.getName() == null) {
            throw new ValidationException("Имя не может быть пустым");
        }
        if (userDto.getEmail() == null) {
            throw new BadRequestException("Email не может быть пустым");
        }
        User user = UserMapper.mapToUser(userDto);
        if (userRepository.getAll().contains(user)) {
            throw new IllegalArgumentException("Пользователь уже существует в базе данных!");
        }
        return UserMapper.mapToUserDto(userRepository.save(user));
    }

    @Override
    public UserDto update(long userId, UserDto userDto) {
        findById(userId);
        User user = UserMapper.mapToUser(userDto);
        for (User dbUser : userRepository.getAll()) {
            if (dbUser.equals(user) && dbUser.getId() != userId) {
                throw new IllegalArgumentException("Пользователь с таким email уже существует!");
            }
        }
        userRepository.update(userId, user);
        User updatedUser = userRepository.findById(userId);
        return UserMapper.mapToUserDto(updatedUser);
    }

    @Override
    public UserDto delete(Long id) {
        findById(id);
        User user = userRepository.findById(id);
        userRepository.delete(id);
        return UserMapper.mapToUserDto(user);
    }

    @Override
    public UserDto findById(long id) {
        return UserMapper.mapToUserDto(userRepository.findById(id));
    }
}
