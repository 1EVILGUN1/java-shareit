package ru.practicum.shareit.user.controller.mapper.model;

import jakarta.validation.constraints.Email;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class UserDto {
    private Long id;
    private String name;
    @Email(message = "Введён некорректный e-mail")
    private String email;
    private Set<Long> items = new HashSet<>();
}
