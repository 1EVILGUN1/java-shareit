package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@ResponseStatus(HttpStatus.BAD_REQUEST)
@RequestMapping(path = "/users")
public class UserController {
    private final UserService service;

    @PostMapping
    public ResponseEntity<UserDto> create(@Valid @RequestBody UserCreateDto user) {
        log.info("Получен запрос POST на добавление пользователя");
        UserDto createUser = service.save(user);
        log.info("Пользователь с Id: {} успешно добавлен! \n {}", createUser.getId(), createUser);
        return new ResponseEntity<>(createUser, HttpStatus.OK);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<UserDto> update(@Valid @RequestBody UserUpdateDto userUpdateDto,
                                          @PathVariable(value = "userId") long userId) {
        log.info("Получен запрос PATCH на обновление данных пользователя с ID: {}", userId);
        UserDto updatedUser = service.update(userId, userUpdateDto);
        log.info("Данные пользователя с ID: {} успешно обновлены! \n {}", updatedUser.getId(), updatedUser);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Collection<UserDto>> getUsers() {
        log.info("Получен запрос GET на получение всех пользователей");
        Collection<UserDto> users = service.getUsers();
        log.info("Вывод всех пользователей /n {}", users);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUserById(@PathVariable(value = "userId") long userId) {
        log.info("Получен запрос GET на вывод пользователя с ID: {}", userId);
        UserDto userDto = service.findById(userId);
        log.info("Вывод пользователя с ID: {}, \n {}", userDto.getId(), userDto);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<UserDto> deleteUser(@PathVariable(value = "userId") long userId) {
        log.info("Получен запрос DELETE на удаление пользователя с ID: {}", userId);
        UserDto deletedUser = service.delete(userId);
        log.info("Пользователь с ID: {} успешно удален! \n {}", deletedUser.getId(), deletedUser);
        return new ResponseEntity<>(deletedUser, HttpStatus.OK);
    }
}
