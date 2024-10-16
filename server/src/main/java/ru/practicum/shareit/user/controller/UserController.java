package ru.practicum.shareit.user.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@AllArgsConstructor
@Slf4j
public class UserController {

    private final UserService service;

    @GetMapping
    public List<UserDto> getAllUsers() {
        List<UserDto> userDtos = service.findAllUsers()
                .stream()
                .map(UserMapper::mapToDto)
                .toList();
        log.info("Get all users {}", userDtos);
        return userDtos;
    }

    @PostMapping
    public User save(@RequestBody User user) {
        User savedUser = service.save(user);
        log.info("Saved user {}", savedUser);
        return savedUser;
    }

    @GetMapping("/{id}")
    public UserDto findById(@PathVariable("id") long id) {
        UserDto userDto = UserMapper.mapToDto(service.findById(id));
        log.info("Find user by id {}", userDto);
        return userDto;
    }

    @PatchMapping("/{id}")
    public User update(@PathVariable("id") long id, @RequestBody User user) {
        User updatedUser = service.update(id, user);
        log.info("Updated user {}", updatedUser);
        return updatedUser;
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") long id) {
        log.info("Delete user by id {}", id);
        service.delete(id);
    }
}
