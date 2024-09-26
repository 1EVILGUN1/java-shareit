package ru.practicum.shareit.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDto> create(@Valid @RequestBody UserDto user) {
        log.info("Получен запрос POST на добавление пользователя");
        UserDto userDto = userService.save(user);
        log.info("Пользователь с Id: {} успешно добавлен!", userDto.getId());
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<UserDto> update(@RequestBody UserDto user,
                                          @PathVariable long userId) {
        log.info("Получен запрос PATCH на обновление данных пользователя с ID: {}", userId);
        UserDto userDto = userService.update(userId, user);
        log.info("Данные пользователя с ID: {} успешно обновлены!", userDto.getId());
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Collection<UserDto>> getAll() {
        log.info("Получен запрос GET на получение всех пользователей");
        Collection<UserDto> users = userService.getAll();
        log.info("Вывод всех пользователей = " + users);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getFindById(@PathVariable(value = "userId") long userId) {
        log.info("Получен запрос GET на получение всех предметов пользователя с ID: {}", userId);
        UserDto userDto = userService.findById(userId);
        log.info("Вывод предметов пользователя с ID: {}", userDto.getId());
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<UserDto> delete(@PathVariable(value = "userId") long userId) {
        log.info("Получен запрос DELETE на удаление пользователя с ID: {}", userId);
        UserDto userDto = userService.delete(userId);
        log.info("Пользователь с ID: {} успешно удален!", userDto.getId());
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }
}
