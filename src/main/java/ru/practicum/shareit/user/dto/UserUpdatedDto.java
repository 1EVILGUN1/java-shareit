package ru.practicum.shareit.user.dto;

import lombok.Getter;

import jakarta.validation.constraints.Email;

@Getter
public class UserUpdatedDto {
    @Email(message = "Введён некоректный e-mail")
    private String email;
    private String name;
}
