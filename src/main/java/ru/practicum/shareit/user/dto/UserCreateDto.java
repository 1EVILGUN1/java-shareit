package ru.practicum.shareit.user.dto;

import lombok.Getter;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Getter
public class UserCreateDto {
    @NotBlank(message = "Имя не может быть пустым!")
    private String name;
    @Email(message = "Введён некоректный e-mail")
    @NotBlank(message = "Email не может быть пустым!")
    private String email;
}
