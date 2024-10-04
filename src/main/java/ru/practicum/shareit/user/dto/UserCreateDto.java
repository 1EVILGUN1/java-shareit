package ru.practicum.shareit.user.dto;

import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
public class UserCreateDto {
    @NotBlank(message = "Имя не может быть пустым!")
    private String name;
    @Email(message = "Введён некоректный e-mail")
    @NotBlank(message = "Email не может быть пустым!")
    private String email;
}
