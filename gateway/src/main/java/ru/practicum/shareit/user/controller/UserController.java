package ru.practicum.shareit.user.controller;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.client.UserClient;
import ru.practicum.shareit.user.dto.UserDto;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("POST Request to search all users");
        return userClient.getAllUsers();
    }

    @PostMapping
    public ResponseEntity<Object> save(@Validated @RequestBody UserDto dto) {
        log.info("POST Request save user with email {}", dto.getEmail());
        return userClient.save(dto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@Positive @PathVariable("id") long id) {
        log.info("GET Request to search user with id {}", id);
        return userClient.findById(id);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@Positive @PathVariable("id") long id, @RequestBody UserDto dto) {
        log.info("PATCH Request update user with id {}", id);
        return userClient.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@Positive @PathVariable("id") long id) {
        log.info("DELETE Request delete user with id {}", id);
        return userClient.delete(id);
    }
}
