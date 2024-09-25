package ru.practicum.shareit.user.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public Collection<UserDto> getAll() {
        Collection<User> result = userRepository.getAll();
        for (User user : result) {
            List<Long> items = itemRepository.getIdItemsUser(user.getId());
            user.setItems(new HashSet<>(items));
        }
        return UserMapper.mapToUserDtoList(result);
    }

    @Override
    public UserDto save(UserDto userDto) {
        User user = UserMapper.mapToUser(userDto);
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
        User user = userRepository.findById(id);
        List<Long> items = itemRepository.getIdItemsUser(id);
        user.setItems(new HashSet<>(items));
        return UserMapper.mapToUserDto(user);
    }
}
