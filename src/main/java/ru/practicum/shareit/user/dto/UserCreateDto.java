package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;


@Getter
public class UserCreateDto {
    @NotBlank(message = "Имя не может быть пустым!")
    private String name;
    @Email(message = "Введён некорректный e-mail")
    @NotBlank(message = "Email не может быть пустым!")
    private String email;
}
