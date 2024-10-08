package ru.practicum.shareit.user.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@AllArgsConstructor
public class UserController {

    private final UserService service;

    @GetMapping
    public List<UserDto> getAllUsers() {
        log.info("GET Запрос на получения списка пользователей");
        List<UserDto> userList = service.findAllUsers()
                .stream()
                .map(UserMapper::mapToDto)
                .toList();
        log.info("Список пользователей {}", userList);
        return userList;
    }

    @PostMapping
    public UserDto saveNewUser(@Valid @RequestBody User user) {
        log.info("POST Запрос на добавление нового пользователя: {}", user);
        UserDto newUser = UserMapper.mapToDto(service.save(user));
        log.info("Новый пользователь сохранен {}", newUser);
        return newUser;
    }

    @GetMapping("/{id}")
    public UserDto findById(@PathVariable("id") long id) {
        log.info("GET Запрос на получение пользователя по ID {}", id);
        UserDto userDto = UserMapper.mapToDto(service.findUserById(id));
        log.info("Пользователь получен {}", userDto);
        return userDto;
    }

    @PatchMapping("/{id}")
    public UserDto update(@PathVariable("id") long id, @RequestBody User user) {
        log.info("PATCH Запрос на обновление пользователя по ID {} \n {}", id, user);
        UserDto updatedUser = UserMapper.mapToDto(service.update(id, user));
        log.info("Запрос на обновление пользователя выполнен {}", updatedUser);
        return updatedUser;
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") long id) {
        log.info("DELETE Запрос на удаление пользователя по ID {}", id);
        User user = service.delete(id);
        log.info("Пользователь удален {}", user);
    }
}
