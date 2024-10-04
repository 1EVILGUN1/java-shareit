package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdatedDto;
import ru.practicum.shareit.user.service.UserService;

import jakarta.validation.Valid;

import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@ResponseStatus(HttpStatus.BAD_REQUEST)
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDto> create(@Valid @RequestBody UserCreateDto user) {
        log.info("Получен запрос POST на добавление пользователя");
        UserDto userDto = userService.create(user);
        log.info("Пользователь с Id: {} успешно добавлен! \n {}", userDto.getId(), userDto);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<UserDto> update(@Valid @RequestBody UserUpdatedDto userUpdatedDto,
                                          @PathVariable(value = "userId") long userId) {
        log.info("Получен запрос PATCH на обновление данных пользователя с ID: {}", userId);
        UserDto userDto = userService.update(userId, userUpdatedDto);
        log.info("Данные пользователя с ID: {} успешно обновлены! \n {}", userDto.getId(), userDto);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Collection<UserDto>> getUsers() {
        log.info("Получен запрос GET на получение всех пользователей");
        Collection<UserDto> userDtos = userService.getUsers();
        log.info("Вывод всех пользователей {}", userDtos);
        return new ResponseEntity<>(userDtos, HttpStatus.OK);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUserById(@PathVariable(value = "userId") long userId) {
        log.info("Получен запрос GET на вывод пользователя с ID: {}", userId);
        UserDto userDto = userService.getUserById(userId);
        log.info("Вывод пользователя с ID: {} \n {}", userDto.getId(), userDto);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<UserDto> delete(@PathVariable(value = "userId") long userId) {
        log.info("Получен запрос DELETE на удаление пользователя с ID: {}", userId);
        UserDto userDto = userService.remove(userId);
        log.info("Пользователь с ID: {} успешно удален! \n {}", userDto.getId(), userDto);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }
}
